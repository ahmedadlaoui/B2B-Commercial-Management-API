package com.example.smartshop.mapper;

import com.example.smartshop.dto.UserDTO;
import com.example.smartshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "client", ignore = true)
    User toEntity(UserDTO userDTO);
}
