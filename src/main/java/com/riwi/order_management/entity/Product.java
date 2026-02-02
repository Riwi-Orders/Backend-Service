package com.riwi.order_management.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    // Decimal(10,2)
    @Column(nullable = false, precision = 10, scale = 2)
    @Check(constraints = "price > 0")
    private double price;

    @Column(nullable = false)
    @Builder.Default
    @Check(constraints = "stock >= 0")
    private int stock = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean is_active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

}
