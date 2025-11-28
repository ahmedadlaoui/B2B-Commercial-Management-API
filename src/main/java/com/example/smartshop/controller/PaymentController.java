package com.example.smartshop.controller;

import com.example.smartshop.dto.payment.PaymentCreateRequest;
import com.example.smartshop.dto.payment.PaymentDTO;
import com.example.smartshop.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthorizationUtil authorizationUtil;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addPayment(
            @Valid @RequestBody PaymentCreateRequest request,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        PaymentDTO payment = paymentService.addPayment(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment added successfully");
        response.put("payment", payment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestParam Long paymentId,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        PaymentDTO payment = paymentService.ProcessPayment(paymentId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment processed successfully");
        response.put("payment", payment);

        return ResponseEntity.ok(response);
    }
}
