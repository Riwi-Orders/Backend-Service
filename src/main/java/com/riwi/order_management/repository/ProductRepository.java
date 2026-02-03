package com.riwi.order_management.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riwi.order_management.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Find all active products (available for sale).
     * 
     * @return list of active products
     */
    List<Product> findByIsActiveTrue();

    /**
     * Find all inactive products.
     * 
     * @return list of inactive products
     */
    List<Product> findByIsActiveFalse();

    /**
     * Check if a product with the given name exists.
     * 
     * @param name the product name to check
     * @return true if a product with this name exists
     */
    boolean existsByName(String name);

    /**
     * Find products by name containing a search term (case insensitive).
     * 
     * @param name the search term
     * @return list of matching products
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}
