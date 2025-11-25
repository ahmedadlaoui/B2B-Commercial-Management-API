package com.example.smartshop.service;

import com.example.smartshop.dto.UserCreateRequest;
import com.example.smartshop.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserCreateRequest request);

    UserDTO getUserById(Long id);

    UserDTO getUserByUsername(String username);

    List<UserDTO> getAllUsers();

    void deleteUser(Long id);

    boolean existsByUsername(String username);
}
