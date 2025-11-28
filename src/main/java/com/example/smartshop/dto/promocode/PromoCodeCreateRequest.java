package com.example.smartshop.dto.promocode;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeCreateRequest {

    @NotBlank(message = "Promo code is required")
    @Size(min = 3, max = 20, message = "Promo code must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Promo code must contain only uppercase letters, numbers, hyphens, and underscores")
    private String code;

    @NotNull(message = "Discount percent is required")
    @DecimalMin(value = "0.01", message = "Discount must be at least 0.01%")
    @DecimalMax(value = "100.00", message = "Discount cannot exceed 100%")
    private BigDecimal discountPercent;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expirationDate;
}
