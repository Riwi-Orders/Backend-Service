package com.riwi.order_management.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.order_management.dto.request.ProductCreateRequest;
import com.riwi.order_management.dto.request.ProductUpdateRequest;
import com.riwi.order_management.dto.response.ApiResponse;
import com.riwi.order_management.dto.response.ProductResponse;
import com.riwi.order_management.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Create a new product (Admin only).
     * POST /api/products
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    /**
     * Get all products. Users see only active products.
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        List<ProductResponse> products = productService.getAllProducts(includeInactive);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * Get active products only (for regular users).
     * GET /api/products/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getActiveProducts() {
        List<ProductResponse> products = productService.getActiveProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * Get product by ID.
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    /**
     * Search products by name.
     * GET /api/products/search?q={searchTerm}
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(@RequestParam String q) {
        List<ProductResponse> products = productService.searchProducts(q);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * Update a product (Admin only).
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    /**
     * Deactivate a product (soft delete - Admin only).
     * PUT /api/products/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(@PathVariable UUID id) {
        productService.deactivateProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deactivated successfully", null));
    }

    /**
     * Delete a product permanently (Admin only).
     * Only allowed if no orders reference this product.
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
