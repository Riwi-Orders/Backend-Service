package com.riwi.order_management.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riwi.order_management.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    /**
     * Find all order items for a specific order.
     * 
     * @param orderId the ID of the order
     * @return list of order items belonging to the order
     */
    List<OrderItem> findByOrderId(UUID orderId);

    /**
     * Find all order items containing a specific product.
     * 
     * @param productId the ID of the product
     * @return list of order items with the given product
     */
    List<OrderItem> findByProductId(UUID productId);

    /**
     * Check if any order items exist for a specific product.
     * Used to prevent deletion of products with existing orders.
     * 
     * @param productId the ID of the product
     * @return true if order items with this product exist
     */
    boolean existsByProductId(UUID productId);
}
