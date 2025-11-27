package com.example.smartshop.dto.client;

import com.example.smartshop.dto.user.UserDTO;
import com.example.smartshop.enums.CustomerTier;
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
public class ClientDTO {

    private Long id;
    private String fullName;
    private String email;
    private CustomerTier tier;
    private BigDecimal totalSpent;
    private Integer totalOrders;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
    private UserDTO user;
}
