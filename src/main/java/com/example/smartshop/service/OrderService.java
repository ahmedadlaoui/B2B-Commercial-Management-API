package com.example.smartshop.service;

import com.example.smartshop.dto.order.OrderCreateRequest;
import com.example.smartshop.dto.order.OrderDTO;
import com.example.smartshop.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderDTO createOrder(OrderCreateRequest request);

    OrderDTO getOrderById(Long id);

    List<OrderDTO> getAllOrders();

    List<OrderDTO> getOrdersByClientId(Long clientId);

    List<OrderDTO> getOrdersByStatus(OrderStatus status);

    OrderDTO confirmOrder(Long id);

    OrderDTO cancelOrder(Long id);

    OrderDTO rejectOrder(Long id);
}
