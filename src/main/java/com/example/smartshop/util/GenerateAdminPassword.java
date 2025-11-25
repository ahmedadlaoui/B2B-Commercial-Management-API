package com.example.smartshop.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GenerateAdminPassword implements CommandLineRunner {

    private final PasswordHashUtil passwordHashUtil;

    public GenerateAdminPassword(PasswordHashUtil passwordHashUtil) {
        this.passwordHashUtil = passwordHashUtil;
    }

    @Override
    public void run(String... args) {
        String password = "admin123";
        String hashedPassword = passwordHashUtil.hashPassword(password);

        System.out.println("\n========================================");
        System.out.println("ADMIN PASSWORD GENERATOR");
        System.out.println("========================================");
        System.out.println("Plain Password: " + password);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("\n========================================");
        System.out.println("SQL QUERY:");
        System.out.println("========================================");
        System.out.println("INSERT INTO users (username, password, role)");
        System.out.println("VALUES ('admin', '" + hashedPassword + "', 'ADMIN');");
        System.out.println("========================================\n");
    }
}
