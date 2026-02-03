package com.riwi.order_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.riwi.order_management.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID id;
    private UUID userId;
    private String userName;
    private OrderStatus status;
    private BigDecimal total;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
