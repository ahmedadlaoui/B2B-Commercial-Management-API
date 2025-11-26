package com.example.smartshop.mapper;

import com.example.smartshop.dto.product.ProductDTO;
import com.example.smartshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    @Mapping(target = "orderItems", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "orderItems", ignore = true)
    void updateEntityFromDTO(ProductDTO productDTO, @MappingTarget Product product);
}
