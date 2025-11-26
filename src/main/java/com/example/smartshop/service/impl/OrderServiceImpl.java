package com.example.smartshop.service.impl;

import com.example.smartshop.dto.order.OrderCreateRequest;
import com.example.smartshop.dto.order.OrderDTO;
import com.example.smartshop.dto.order.OrderItemRequest;
import com.example.smartshop.entity.*;
import com.example.smartshop.enums.CustomerTier;
import com.example.smartshop.enums.OrderStatus;
import com.example.smartshop.exception.BusinessException;
import com.example.smartshop.exception.BusinessRuleViolationException;
import com.example.smartshop.exception.ResourceNotFoundException;
import com.example.smartshop.mapper.OrderMapper;
import com.example.smartshop.repository.*;
import com.example.smartshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final AppConfigRepository appConfigRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderCreateRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + request.getClientId()));

        AppConfig config = appConfigRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("Application configuration not found"));

        Order order = Order.builder()
                .client(client)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findByIdAndNotDeleted(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessRuleViolationException(
                        "Insufficient stock for product: " + product.getName() + 
                        ". Available: " + product.getStock() + ", Requested: " + itemRequest.getQuantity());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .build();

            orderItems.add(orderItem);
            subTotal = subTotal.add(itemTotal);

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        order.setOrderItems(orderItems);
        order.setSubTotal(subTotal);

        BigDecimal tierDiscountPercent = getTierDiscountPercent(client.getTier(), config);
        BigDecimal tierDiscountAmount = subTotal.multiply(tierDiscountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal promoDiscountAmount = BigDecimal.ZERO;
        if (request.getPromoCode() != null && !request.getPromoCode().trim().isEmpty()) {
            PromoCode promoCode = promoCodeRepository.findByCodeAndActive(request.getPromoCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Promo code not found or inactive: " + request.getPromoCode()));

            if (promoCode.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new BusinessRuleViolationException("Promo code has expired");
            }

            order.setPromoCode(promoCode);
            order.setAppliedPromoCode(promoCode.getCode());
            promoDiscountAmount = subTotal.multiply(promoCode.getDiscountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            order.setPromoCodeAmount(promoDiscountAmount);
        }

        BigDecimal totalDiscountAmount = tierDiscountAmount.add(promoDiscountAmount);
        BigDecimal netAmount = subTotal.subtract(totalDiscountAmount);
        BigDecimal taxAmount = netAmount.multiply(config.getTvaPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = netAmount.add(taxAmount);

        order.setDiscountAmount(totalDiscountAmount);
        order.setNetAmount(netAmount);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);
        order.setRemainingAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    private BigDecimal getTierDiscountPercent(CustomerTier tier, AppConfig config) {
        return switch (tier) {
            case SILVER -> config.getSilverDiscountPercent();
            case GOLD -> config.getGoldDiscountPercent();
            case PLATINUM -> config.getPlatinumDiscountPercent();
            default -> BigDecimal.ZERO;
        };
    }
}

