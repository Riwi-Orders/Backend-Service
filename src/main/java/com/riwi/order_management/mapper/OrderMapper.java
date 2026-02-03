package com.riwi.order_management.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.riwi.order_management.dto.response.OrderItemResponse;
import com.riwi.order_management.dto.response.OrderResponse;
import com.riwi.order_management.entity.Order;
import com.riwi.order_management.entity.OrderItem;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .status(order.getStatus())
                .total(order.getTotal())
                .items(order.getOrderItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .build();
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
