package com.example.smartshop.service;

import com.example.smartshop.dto.order.OrderCreateRequest;
import com.example.smartshop.dto.order.OrderDTO;

public interface OrderService {

    OrderDTO createOrder(OrderCreateRequest request);
}
