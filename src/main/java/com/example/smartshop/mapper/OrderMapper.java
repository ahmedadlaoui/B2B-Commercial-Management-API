package com.example.smartshop.mapper;

import com.example.smartshop.dto.order.OrderDTO;
import com.example.smartshop.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.fullName", target = "clientName")
    OrderDTO toDTO(Order order);
}
