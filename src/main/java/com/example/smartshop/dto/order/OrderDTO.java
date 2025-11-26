package com.example.smartshop.dto.order;

import com.example.smartshop.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private Long clientId;
    private String clientName;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private String appliedPromoCode;
    private BigDecimal promoCodeAmount;
    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private List<OrderItemDTO> orderItems;
}
