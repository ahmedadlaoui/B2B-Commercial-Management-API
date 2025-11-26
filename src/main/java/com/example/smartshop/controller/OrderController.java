package com.example.smartshop.controller;

import com.example.smartshop.dto.order.OrderCreateRequest;
import com.example.smartshop.dto.order.OrderDTO;
import com.example.smartshop.service.OrderService;
import com.example.smartshop.util.AuthorizationUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthorizationUtil authorizationUtil;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        OrderDTO order = orderService.createOrder(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order created successfully");
        response.put("order", order);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
