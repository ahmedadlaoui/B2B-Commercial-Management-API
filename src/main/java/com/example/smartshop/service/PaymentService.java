package com.example.smartshop.service;

import com.example.smartshop.dto.payment.PaymentCreateRequest;
import com.example.smartshop.dto.payment.PaymentDTO;

import java.util.List;

public interface PaymentService {

    PaymentDTO addPayment(PaymentCreateRequest request);

    PaymentDTO ProcessPayment(long paymentId);

    PaymentDTO getPaymentById(Long id);

    List<PaymentDTO> getPaymentsByOrderId(Long orderId);

    List<PaymentDTO> getAllPayments();
}
