package com.example.smartshop.mapper;

import com.example.smartshop.dto.ClientDTO;
import com.example.smartshop.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ClientMapper {

    ClientDTO toDTO(Client client);

    @Mapping(target = "orders", ignore = true)
    Client toEntity(ClientDTO clientDTO);
}
