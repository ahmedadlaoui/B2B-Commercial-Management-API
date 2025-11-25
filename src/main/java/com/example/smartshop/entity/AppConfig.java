package com.example.smartshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "app_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tvaPercent;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal silverDiscountPercent;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal goldDiscountPercent;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal platinumDiscountPercent;
}
