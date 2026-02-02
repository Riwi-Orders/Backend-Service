package com.riwi.order_management.entity;

import java.util.UUID;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @OneToOne(mappedBy = "orderItem")
    private Order order;

    @Column(nullable = false)
    private Product product;

    @Column(nullable = false)
    @Check(constraints = "quantity >= 0")
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private double price;

}
