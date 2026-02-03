package com.riwi.order_management.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.order_management.dto.request.OrderCreateRequest;
import com.riwi.order_management.dto.request.OrderStatusUpdateRequest;
import com.riwi.order_management.dto.response.ApiResponse;
import com.riwi.order_management.dto.response.OrderResponse;
import com.riwi.order_management.entity.OrderStatus;
import com.riwi.order_management.security.UserPrincipal;
import com.riwi.order_management.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order (User only - Admin cannot create orders).
     * POST /api/orders
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OrderCreateRequest request) {
        OrderResponse order = orderService.createOrder(principal.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", order));
    }

    /**
     * Get all orders (Admin only).
     * GET /api/orders
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * Get orders by status (Admin only).
     * GET /api/orders/status?status={status}
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus(
            @RequestParam OrderStatus status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * Get current user's orders (User only).
     * GET /api/orders/my-orders
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * Get order by ID.
     * Users can only see their own orders. Admins can see all orders.
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        OrderResponse order;

        if (principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            order = orderService.getOrderById(id);
        } else {
            order = orderService.getOrderByIdForUser(id, principal.getId());
        }

        return ResponseEntity.ok(ApiResponse.success(order));
    }

    /**
     * Update order status (Admin only).
     * PUT /api/orders/{id}/status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse order = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
    }

    /**
     * Cancel an order (User can only cancel their own PENDING orders).
     * PUT /api/orders/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        OrderResponse order = orderService.cancelOrder(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }
}
