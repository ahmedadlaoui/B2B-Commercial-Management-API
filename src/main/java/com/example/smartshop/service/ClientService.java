package com.example.smartshop.service;

import com.example.smartshop.dto.ClientCreateRequest;
import com.example.smartshop.dto.ClientDTO;

import java.util.List;

public interface ClientService {

    ClientDTO createClient(ClientCreateRequest request);

    ClientDTO getClientById(Long id);

    ClientDTO getClientByUserId(Long userId);

    ClientDTO getClientByEmail(String email);

    List<ClientDTO> getAllClients();

    void deleteClient(Long id);

    boolean existsByEmail(String email);
}
