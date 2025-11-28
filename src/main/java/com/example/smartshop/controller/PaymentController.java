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
import java.util.List;
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

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPaymentById(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        PaymentDTO payment = paymentService.getPaymentById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("payment", payment);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPayments(
            @RequestParam(required = false) Long orderId,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        List<PaymentDTO> payments;

        if (orderId != null) {
            payments = paymentService.getPaymentsByOrderId(orderId);
        } else {
            payments = paymentService.getAllPayments();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("payments", payments);
        response.put("count", payments.size());

        return ResponseEntity.ok(response);
    }
}
