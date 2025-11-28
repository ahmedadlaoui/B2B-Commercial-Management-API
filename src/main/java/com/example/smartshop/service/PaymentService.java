package com.example.smartshop.service;

import com.example.smartshop.dto.payment.PaymentCreateRequest;
import com.example.smartshop.dto.payment.PaymentDTO;

public interface PaymentService {

    PaymentDTO addPayment(PaymentCreateRequest request);
    PaymentDTO ProcessPayment(long paymentId);
}
