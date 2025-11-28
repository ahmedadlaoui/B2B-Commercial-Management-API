package com.example.smartshop.service.impl;

import com.example.smartshop.dto.payment.PaymentCreateRequest;
import com.example.smartshop.dto.payment.PaymentDTO;
import com.example.smartshop.entity.Order;
import com.example.smartshop.entity.Payment;
import com.example.smartshop.enums.OrderStatus;
import com.example.smartshop.enums.PaymentMethod;
import com.example.smartshop.enums.PaymentStatus;
import com.example.smartshop.exception.BusinessRuleViolationException;
import com.example.smartshop.exception.ResourceNotFoundException;
import com.example.smartshop.mapper.PaymentMapper;
import com.example.smartshop.repository.OrderRepository;
import com.example.smartshop.repository.PaymentRepository;
import com.example.smartshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDTO addPayment(PaymentCreateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + request.getOrderId()));

        if (request.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new BusinessRuleViolationException("Payment amount exceeds the remaining order balance.");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .referenceNumber(request.getReferenceNumber())
                .build();

        if (payment.getMethod() == PaymentMethod.CASH) {
            payment.setStatus(PaymentStatus.PROCESSED);
            updateOrderAfterPayment(order, payment.getAmount());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDTO(savedPayment);
    }

    @Override
    @Transactional
    public PaymentDTO ProcessPayment(long paymentID) {
        Payment paymentToProcess = paymentRepository.findById(paymentID)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentID));

        if (paymentToProcess.getStatus() == PaymentStatus.PROCESSED) {
            throw new BusinessRuleViolationException("Payment already processed");
        }

        paymentToProcess.setStatus(PaymentStatus.PROCESSED);
        updateOrderAfterPayment(paymentToProcess.getOrder(), paymentToProcess.getAmount());

        Payment savedPayment = paymentRepository.save(paymentToProcess);
        return paymentMapper.toDTO(savedPayment);
    }

    private void updateOrderAfterPayment(Order order, BigDecimal amountPaid) {
        BigDecimal newRemaining = order.getRemainingAmount().subtract(amountPaid);
        order.setRemainingAmount(newRemaining);

        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
    }
}