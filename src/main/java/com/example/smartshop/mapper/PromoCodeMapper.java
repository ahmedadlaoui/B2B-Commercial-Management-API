package com.example.smartshop.mapper;

import com.example.smartshop.dto.promocode.PromoCodeDTO;
import com.example.smartshop.entity.PromoCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {

    @Mapping(target = "usageCount", expression = "java(promoCode.getOrders() != null ? promoCode.getOrders().size() : 0)")
    PromoCodeDTO toDTO(PromoCode promoCode);
}
