package com.marketplace.ms_order.service;

import com.marketplace.ms_order.client.CartClient;
import com.marketplace.ms_order.client.PaymentClient;
import com.marketplace.ms_order.dto.CartItemDto;
import com.marketplace.ms_order.model.Order;
import com.marketplace.ms_order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartClient cartClient;
    @Mock private PaymentClient paymentClient;
    @InjectMocks private OrderService orderService;

    @Test
    void saveOrder_debeEstablecerEstadoPending() {
        // Given
        Order order = Order.builder().userId(1L).build();
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Order saved = orderService.saveOrder(order);

        // Then
        assertEquals("PENDING", saved.getStatus());
    }

    @Test
    void checkout_conCarritoVacio_debeLanzarExcepcion() {
        // Given
        when(cartClient.getCartByUser(1L, null)).thenReturn(List.of());

        // When / Then
        assertThrows(RuntimeException.class, () -> orderService.checkout(1L));
    }

    @Test
    void checkout_conCarritoValido_debeMarcarComoPaid() {
        // Given
        CartItemDto item = CartItemDto.builder().productId(2L).quantity(2).build();
        Order saved = Order.builder().id(5L).userId(1L).status("PENDING").build();

        when(cartClient.getCartByUser(1L, null)).thenReturn(List.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);
        when(paymentClient.processPayment(5L, null)).thenReturn("APPROVED");

        // When
        Order result = orderService.checkout(1L);

        // Then
        assertEquals("PAID", result.getStatus());
        verify(paymentClient).processPayment(5L, null);
    }

    @Test
    void deleteOrder_cuandoNoExiste_debeLanzarExcepcion() {
        // Given
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> orderService.deleteOrder(99L));
    }
}
