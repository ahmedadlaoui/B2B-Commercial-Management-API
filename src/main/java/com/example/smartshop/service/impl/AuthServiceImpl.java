package com.example.smartshop.service.impl;

import com.example.smartshop.dto.UserDTO;
import com.example.smartshop.entity.User;
import com.example.smartshop.exception.InvalidCredentialsException;
import com.example.smartshop.mapper.UserMapper;
import com.example.smartshop.repository.UserRepository;
import com.example.smartshop.service.AuthService;
import com.example.smartshop.util.PasswordHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordHashUtil passwordHashUtil;

    @Override
    @Transactional(readOnly = true)
    public UserDTO login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHashUtil.verifyPassword(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        log.info("User {} logged in successfully with role {}", user.getUsername(), user.getRole());

        return userMapper.toDTO(user);
    }

    @Override
    public void logout(Long userId) {
        log.info("User with ID {} logged out", userId);
    }
}
