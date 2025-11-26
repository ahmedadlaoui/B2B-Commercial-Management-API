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
                .orElseThrow(() -> new BusinessException("App configuration missing"));

        PromoCode promoCode = validatePromoCode(request.getPromoCode());

        Order order = Order.builder()
                .client(client)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal subTotal = processOrderItems(order, request.getItems());
        order.setSubTotal(subTotal);

        BigDecimal tierDiscountAmount = calculateTierDiscount(client.getTier(), subTotal, config);
        BigDecimal promoDiscountAmount = calculatePromoDiscount(promoCode, subTotal, order);
        BigDecimal totalDiscountAmount = tierDiscountAmount.add(promoDiscountAmount);

        BigDecimal netAmount = subTotal.subtract(totalDiscountAmount);
        BigDecimal taxAmount = netAmount.multiply(config.getTvaPercent()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = netAmount.add(taxAmount);

        order.setDiscountAmount(totalDiscountAmount);
        order.setNetAmount(netAmount);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);
        order.setRemainingAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    private PromoCode validatePromoCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        PromoCode promoCode = promoCodeRepository.findByCodeAndActive(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found or inactive: " + code));

        if (promoCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleViolationException("Promo code has expired");
        }

        return promoCode;
    }

    private BigDecimal processOrderItems(Order order, List<OrderItemRequest> itemRequests) {
        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : itemRequests) {
            Product product = productRepository.findByIdAndNotDeleted(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with ID: " + itemRequest.getProductId()));

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
        return subTotal;
    }

    private BigDecimal calculateTierDiscount(CustomerTier tier, BigDecimal subTotal, AppConfig config) {
        BigDecimal tierDiscountPercent = getTierDiscountPercent(tier, config);
        return subTotal.multiply(tierDiscountPercent).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePromoDiscount(PromoCode promoCode, BigDecimal subTotal, Order order) {
        if (promoCode == null) {
            return BigDecimal.ZERO;
        }

        order.setPromoCode(promoCode);
        order.setAppliedPromoCode(promoCode.getCode());

        BigDecimal promoDiscountAmount = subTotal.multiply(promoCode.getDiscountPercent()).setScale(2,
                RoundingMode.HALF_UP);
        order.setPromoCodeAmount(promoDiscountAmount);

        return promoDiscountAmount;
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
