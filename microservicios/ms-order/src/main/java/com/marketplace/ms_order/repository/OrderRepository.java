package com.marketplace.ms_order.repository;

import com.marketplace.ms_order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Para buscar las órdenes de un usuario específico
    List<Order> findByUserId(Long userId);
}