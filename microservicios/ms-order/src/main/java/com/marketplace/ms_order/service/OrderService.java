package com.marketplace.ms_order.service;

import com.marketplace.ms_order.client.CartClient;
import com.marketplace.ms_order.client.PaymentClient;
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
    private final PaymentClient paymentClient;  

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
        List<CartItemDto> cartItems = cartClient.getCartByUser(userId);
        if(cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        double total = 0;
        for(CartItemDto item : cartItems) {
            total += item.getQuantity() * 10000; // Simulamos un precio fijo por producto para este ejemplo
        }
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        log.info("Processing payment for order {}", savedOrder.getId());
        String paymentStatus =
                paymentClient.processPayment(savedOrder.getId());
        log.info("Payment response: {}", paymentStatus);
        if(paymentStatus.equals("APPROVED")) {
            savedOrder.setStatus("CONFIRMED");
        } else {
            savedOrder.setStatus("REJECTED");
        }
        log.info("Checkout completed for user {}", userId);
        return orderRepository.save(savedOrder);
    }
}