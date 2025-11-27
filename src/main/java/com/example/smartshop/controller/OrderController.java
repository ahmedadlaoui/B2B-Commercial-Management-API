package com.example.smartshop.controller;

import com.example.smartshop.dto.order.OrderCreateRequest;
import com.example.smartshop.dto.order.OrderDTO;
import com.example.smartshop.enums.OrderStatus;
import com.example.smartshop.service.OrderService;
import com.example.smartshop.util.AuthorizationUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        OrderDTO order = orderService.getOrderById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("order", order);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) OrderStatus status,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        List<OrderDTO> orders;

        if (clientId != null) {
            orders = orderService.getOrdersByClientId(clientId);
        } else if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("count", orders.size());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmOrder(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        OrderDTO order = orderService.confirmOrder(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order confirmed successfully");
        response.put("order", order);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        OrderDTO order = orderService.cancelOrder(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order canceled successfully");
        response.put("order", order);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectOrder(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        OrderDTO order = orderService.rejectOrder(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order rejected successfully");
        response.put("order", order);

        return ResponseEntity.ok(response);
    }
}
