package com.example.smartshop.mapper;

import com.example.smartshop.dto.payment.PaymentDTO;
import com.example.smartshop.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentDTO toDTO(Payment payment);
}
