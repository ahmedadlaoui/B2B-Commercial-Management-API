package com.example.smartshop.service;

import com.example.smartshop.dto.UserDTO;

public interface AuthService {

    UserDTO login(String username, String password);

    void logout(Long userId);
}
