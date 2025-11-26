package com.example.smartshop.service.impl;

import com.example.smartshop.dto.client.ClientCreateRequest;
import com.example.smartshop.dto.client.ClientDTO;
import com.example.smartshop.dto.client.ClientUpdateRequest;
import com.example.smartshop.entity.Client;
import com.example.smartshop.entity.User;
import com.example.smartshop.enums.CustomerTier;
import com.example.smartshop.enums.UserRole;
import com.example.smartshop.exception.DuplicateResourceException;
import com.example.smartshop.exception.ResourceNotFoundException;
import com.example.smartshop.mapper.ClientMapper;
import com.example.smartshop.repository.ClientRepository;
import com.example.smartshop.repository.UserRepository;
import com.example.smartshop.service.ClientService;
import com.example.smartshop.util.PasswordHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final PasswordHashUtil passwordHashUtil;

    @Override
    public ClientDTO createClient(ClientCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Client", "email", request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordHashUtil.hashPassword(request.getPassword()))
                .role(UserRole.CLIENT)
                .build();

        User savedUser = userRepository.save(user);

        Client client = Client.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .tier(CustomerTier.BASIC)
                .totalSpent(BigDecimal.ZERO)
                .totalOrders(0)
                .user(savedUser)
                .build();

        Client savedClient = clientRepository.save(client);
        return clientMapper.toDTO(savedClient);
    }

    @Override
    public ClientDTO updateClient(Long id, ClientUpdateRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        if (request.getEmail() != null && !request.getEmail().equals(client.getEmail())) {
            if (clientRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Client", "email", request.getEmail());
            }
            client.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            client.setFullName(request.getFullName());
        }

        if (request.getTier() != null) {
            client.setTier(request.getTier());
        }

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toDTO(updatedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        return clientMapper.toDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientByUserId(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "userId", userId));
        return clientMapper.toDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", email));
        return clientMapper.toDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        userRepository.deleteById(client.getUser().getId());
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return clientRepository.existsByEmail(email);
    }
}
