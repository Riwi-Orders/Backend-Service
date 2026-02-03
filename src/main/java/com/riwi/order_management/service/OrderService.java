package com.riwi.order_management.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riwi.order_management.dto.request.OrderCreateRequest;
import com.riwi.order_management.dto.request.OrderItemRequest;
import com.riwi.order_management.dto.response.OrderResponse;
import com.riwi.order_management.entity.Order;
import com.riwi.order_management.entity.OrderItem;
import com.riwi.order_management.entity.OrderStatus;
import com.riwi.order_management.entity.Product;
import com.riwi.order_management.entity.User;
import com.riwi.order_management.exception.BusinessException;
import com.riwi.order_management.exception.ResourceNotFoundException;
import com.riwi.order_management.exception.UnauthorizedException;
import com.riwi.order_management.mapper.OrderMapper;
import com.riwi.order_management.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;
    private final OrderMapper orderMapper;

    /**
     * Creates a new order for a user.
     * 
     * @param userId  the ID of the user creating the order
     * @param request the order creation data
     * @return the created order response
     */
    public OrderResponse createOrder(UUID userId, OrderCreateRequest request) {
        User user = userService.getUserEntityById(userId);

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .build();

        // Add order items
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductEntityById(itemRequest.getProductId());

            // Validate product is active
            if (!product.getIsActive()) {
                throw new BusinessException("Product is not available: " + product.getName());
            }

            // Validate stock
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice()) // Copy historical price
                    .build();

            order.addOrderItem(orderItem);

            // Reduce stock
            productService.reduceStock(product.getId(), itemRequest.getQuantity());
        }

        // Calculate total in backend
        order.calculateTotal();

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Gets all orders (Admin only).
     * 
     * @return list of all orders
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Gets all orders for a specific user.
     * 
     * @param userId the user ID
     * @return list of orders belonging to the user
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Gets an order by ID.
     * 
     * @param id the order ID
     * @return the order response
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = getOrderEntityById(id);
        return orderMapper.toResponse(order);
    }

    /**
     * Gets an order by ID for a specific user (validates ownership).
     * 
     * @param orderId the order ID
     * @param userId  the user ID
     * @return the order response
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForUser(UUID orderId, UUID userId) {
        Order order = getOrderEntityById(orderId);

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to view this order");
        }

        return orderMapper.toResponse(order);
    }

    /**
     * Gets the order entity by ID (internal use).
     * 
     * @param id the order ID
     * @return the order entity
     */
    @Transactional(readOnly = true)
    public Order getOrderEntityById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    /**
     * Updates order status (Admin only).
     * 
     * @param orderId   the order ID
     * @param newStatus the new status
     * @return the updated order response
     */
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = getOrderEntityById(orderId);
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Cancels an order (User can only cancel PENDING orders).
     * 
     * @param orderId the order ID
     * @param userId  the user ID requesting cancellation
     * @return the updated order response
     */
    public OrderResponse cancelOrder(UUID orderId, UUID userId) {
        Order order = getOrderEntityById(orderId);

        // Validate ownership
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to cancel this order");
        }

        // Validate status - users can only cancel PENDING orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException(
                    "Can only cancel orders with PENDING status. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Gets orders by status (Admin only).
     * 
     * @param status the order status to filter by
     * @return list of orders with the given status
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}
