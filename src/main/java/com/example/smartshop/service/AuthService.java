package com.example.smartshop.service;

import com.example.smartshop.dto.UserDTO;
import jakarta.servlet.http.HttpSession;

public interface AuthService {

    UserDTO login(String username, String password, HttpSession session);

    void logout(HttpSession session);
}
