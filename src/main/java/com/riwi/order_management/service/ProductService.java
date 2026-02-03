package com.riwi.order_management.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riwi.order_management.dto.request.ProductCreateRequest;
import com.riwi.order_management.dto.request.ProductUpdateRequest;
import com.riwi.order_management.dto.response.ProductResponse;
import com.riwi.order_management.entity.Product;
import com.riwi.order_management.exception.BusinessException;
import com.riwi.order_management.exception.ResourceNotFoundException;
import com.riwi.order_management.mapper.ProductMapper;
import com.riwi.order_management.repository.OrderItemRepository;
import com.riwi.order_management.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;

    /**
     * Creates a new product (Admin only).
     * 
     * @param request the product creation data
     * @return the created product response
     */
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    /**
     * Gets all products (Admin sees all, Users see only active).
     * 
     * @param includeInactive whether to include inactive products
     * @return list of products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(boolean includeInactive) {
        List<Product> products;
        if (includeInactive) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByIsActiveTrue();
        }
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    /**
     * Gets active products only (for regular users).
     * 
     * @return list of active products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        return getAllProducts(false);
    }

    /**
     * Gets a product by ID.
     * 
     * @param id the product ID
     * @return the product response
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = getProductEntityById(id);
        return productMapper.toResponse(product);
    }

    /**
     * Gets the product entity by ID (internal use).
     * 
     * @param id the product ID
     * @return the product entity
     */
    @Transactional(readOnly = true)
    public Product getProductEntityById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    /**
     * Updates a product (Admin only).
     * 
     * @param id      the product ID to update
     * @param request the update data
     * @return the updated product response
     */
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = getProductEntityById(id);

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    /**
     * Deactivates a product (soft delete). Products with orders cannot be deleted.
     * 
     * @param id the product ID to deactivate
     */
    public void deactivateProduct(UUID id) {
        Product product = getProductEntityById(id);
        product.setIsActive(false);
        productRepository.save(product);
    }

    /**
     * Deletes a product permanently. Only allowed if no orders reference this
     * product.
     * 
     * @param id the product ID to delete
     */
    public void deleteProduct(UUID id) {
        Product product = getProductEntityById(id);

        // Check if product has any orders
        if (orderItemRepository.existsByProductId(id)) {
            throw new BusinessException("Cannot delete product with existing orders. Consider deactivating instead.");
        }

        productRepository.delete(product);
    }

    /**
     * Searches products by name.
     * 
     * @param searchTerm the search term
     * @return list of matching products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String searchTerm) {
        return productRepository.findByNameContainingIgnoreCase(searchTerm).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    /**
     * Reduces product stock after order is placed.
     * 
     * @param productId the product ID
     * @param quantity  the quantity to reduce
     */
    public void reduceStock(UUID productId, int quantity) {
        Product product = getProductEntityById(productId);

        if (product.getStock() < quantity) {
            throw new BusinessException("Insufficient stock for product: " + product.getName());
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
