package com.example.smartshop.dto.promocode;

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
public class PromoCodeDTO {

    private Long id;
    private String code;
    private BigDecimal discountPercent;
    private LocalDateTime expirationDate;
    private Boolean active;
    private Integer usageCount;
}
