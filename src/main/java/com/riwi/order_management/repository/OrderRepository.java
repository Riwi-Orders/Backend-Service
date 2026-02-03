package com.riwi.order_management.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riwi.order_management.entity.Order;
import com.riwi.order_management.entity.OrderStatus;
import com.riwi.order_management.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Find all orders for a specific user.
     * 
     * @param user the user whose orders to find
     * @return list of orders belonging to the user
     */
    List<Order> findByUser(User user);

    /**
     * Find all orders for a specific user by user ID.
     * 
     * @param userId the ID of the user
     * @return list of orders belonging to the user
     */
    List<Order> findByUserId(UUID userId);

    /**
     * Find all orders with a specific status.
     * 
     * @param status the order status to filter by
     * @return list of orders with the given status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find all orders for a specific user with a specific status.
     * 
     * @param userId the ID of the user
     * @param status the order status to filter by
     * @return list of matching orders
     */
    List<Order> findByUserIdAndStatus(UUID userId, OrderStatus status);
}
