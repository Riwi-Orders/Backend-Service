package com.riwi.order_management.mapper;

import org.springframework.stereotype.Component;

import com.riwi.order_management.dto.request.ProductCreateRequest;
import com.riwi.order_management.dto.response.ProductResponse;
import com.riwi.order_management.entity.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .isActive(true)
                .build();
    }
}
