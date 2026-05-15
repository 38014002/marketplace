package com.marketplace.ms_order.service;

import com.marketplace.ms_order.client.CartClient;
import com.marketplace.ms_order.model.Order;
import com.marketplace.ms_order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.marketplace.ms_order.dto.CartItemDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;

    public Order saveOrder(Order order) {
        order.setStatus("PENDING");
        log.info("Saving order: {}", order);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUser(Long userId) {
        log.info("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId);
    }

    public Order checkout(Long userId) {
        log.info("Getting cart for user {}", userId);
        List<CartItemDto> cartItems =
                cartClient.getCartByUser(userId);
        if(cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        double total = 0;
        for(CartItemDto item : cartItems) {
            total += item.getQuantity() * 10000;
        }
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setTotalAmount(total);
        log.info("Checkout completed for user {}", userId);
        return orderRepository.save(order);
    }
}