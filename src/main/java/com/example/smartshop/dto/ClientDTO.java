package com.example.smartshop.dto;

import com.example.smartshop.enums.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    private Long id;
    private String fullName;
    private String email;
    private CustomerTier tier;
    private BigDecimal totalSpent;
    private Integer totalOrders;
    private UserDTO user;
}
