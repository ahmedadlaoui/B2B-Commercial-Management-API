package com.example.smartshop.mapper;

import com.example.smartshop.dto.order.OrderItemDTO;
import com.example.smartshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemDTO toDTO(OrderItem orderItem);
}
