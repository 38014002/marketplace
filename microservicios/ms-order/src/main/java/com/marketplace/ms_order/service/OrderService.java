package com.marketplace.ms_order.service;

import com.marketplace.ms_order.client.CartClient;
import com.marketplace.ms_order.client.PaymentClient;
import com.marketplace.ms_order.dto.CartItemDto;
import com.marketplace.ms_order.model.Order;
import com.marketplace.ms_order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final PaymentClient paymentClient;

    @Transactional
    public Order saveOrder(Order order) {
        log.info("Registrando orden manual en estado PENDING");
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        log.info("Obteniendo el listado global de órdenes");
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(Long userId) {
        log.info("Buscando órdenes del usuario con ID: {}", userId);
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order checkout(Long userId) {
        log.info("Iniciando checkout para el usuario: {}", userId);

        String token = obtenerTokenJwt();
        List<CartItemDto> carrito = cartClient.getCartByUser(userId, token);

        if (carrito == null || carrito.isEmpty()) {
            throw new RuntimeException("El proceso no puede continuar: El carrito está vacío");
        }

        List<Long> productIds = carrito.stream()
                .map(CartItemDto::getProductId)
                .collect(Collectors.toList());

        Order orden = Order.builder()
                .userId(userId)
                .status("PENDING")
                .totalAmount(calcularTotal(carrito))
                .productIds(productIds)
                .build();

        Order ordenGuardada = orderRepository.save(orden);
        log.info("Orden preliminar guardada con ID: {}. Procesando pago...", ordenGuardada.getId());

        String respuestaPago = paymentClient.processPayment(ordenGuardada.getId(), token);
        log.info("Respuesta recibida del sistema de pagos: {}", respuestaPago);

        if (respuestaPago != null && respuestaPago.toUpperCase().contains("APPROVED")) {
            ordenGuardada.setStatus("PAID");
        } else {
            ordenGuardada.setStatus("CANCELED");
        }

        log.info("Checkout finalizado para el usuario: {}", userId);
        return orderRepository.save(ordenGuardada);
    }

    @Transactional
    public Order updateOrder(Long id, Order datosNuevos) {
        log.info("Buscando orden ID: {} para actualizar", id);
        Order ordenExistente = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con el ID: " + id));

        ordenExistente.setUserId(datosNuevos.getUserId());
        ordenExistente.setStatus(datosNuevos.getStatus());
        ordenExistente.setTotalAmount(datosNuevos.getTotalAmount());
        ordenExistente.setProductIds(datosNuevos.getProductIds());

        return orderRepository.save(ordenExistente);
    }

    @Transactional
    public void deleteOrder(Long id) {
        log.info("Buscando orden ID: {} para eliminación", id);
        Order orden = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con el ID: " + id));

        orderRepository.delete(orden);
        log.info("Orden con ID: {} eliminada correctamente", id);
    }

    private Double calcularTotal(List<CartItemDto> items) {
        double total = 0;
        for (CartItemDto item : items) {
            total += item.getQuantity() * 10000;
        }
        return total;
    }

    private String obtenerTokenJwt() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
