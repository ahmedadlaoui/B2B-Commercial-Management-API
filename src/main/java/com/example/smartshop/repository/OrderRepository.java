package com.example.smartshop.repository;

import com.example.smartshop.entity.Order;
import com.example.smartshop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.client.id = :clientId AND o.status = :status")
    boolean existsByClientIdAndStatus(Long clientId, OrderStatus status);

    List<Order> findByClientId(Long clientId);

    List<Order> findByStatus(OrderStatus status);
}
