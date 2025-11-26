package com.example.smartshop.util;

import com.example.smartshop.enums.UserRole;
import com.example.smartshop.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtil {

    public void requireLogin(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedAccessException("Please login first");
        }
    }

    public void requireRole(HttpSession session, UserRole requiredRole) {
        requireLogin(session);

        String roleStr = (String) session.getAttribute("role");
        if (roleStr == null || !roleStr.equals(requiredRole.name())) {
            throw new UnauthorizedAccessException("You do not have permission to access this resource");
        }
    }

    public void requireAdmin(HttpSession session) {
        requireRole(session, UserRole.ADMIN);
    }

    public Long getCurrentUserId(HttpSession session) {
        requireLogin(session);
        return (Long) session.getAttribute("userId");
    }

    public String getCurrentUsername(HttpSession session) {
        requireLogin(session);
        return (String) session.getAttribute("username");
    }

    public UserRole getCurrentUserRole(HttpSession session) {
        requireLogin(session);
        String roleStr = (String) session.getAttribute("role");
        return UserRole.valueOf(roleStr);
    }
}
