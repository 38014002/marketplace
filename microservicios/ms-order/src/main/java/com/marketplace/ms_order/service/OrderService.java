package com.marketplace.ms_order.service;

import com.marketplace.ms_order.dto.CartItemDto;
import com.marketplace.ms_order.model.Order;
import com.marketplace.ms_order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    // 1. GUARDAR ÓRDEN MANUALMENTE
    @Transactional
    public Order saveOrder(Order order) {
        log.info("Registrando orden manual en estado PENDING");
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }

    // 2. OBTENER TODAS LAS ÓRDENES (Para uso del ADMIN)
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        log.info("Obteniendo el listado global de órdenes");
        return orderRepository.findAll();
    }

    // 3. OBTENER ÓRDENES DE UN USUARIO ESPECÍFICO
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(Long userId) {
        log.info("Buscando órdenes del usuario con ID: {}", userId);
        return orderRepository.findByUserId(userId);
    }

    // 4. PROCESAR CHECKOUT (Sincronizado con el modelo Order y ms-cart)
    @Transactional
    public Order checkout(Long userId) {
        log.info("Iniciando checkout con WebClient para el usuario: {}", userId);

        // Paso A: Obtener el carrito desde ms-cart consumiendo su API REST
        List<CartItemDto> carrito = webClientBuilder.build()
                .get()
                .uri("http://ms-cart/api/cart/user/{userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CartItemDto>>() {
                })
                .block();

        if (carrito == null || carrito.isEmpty()) {
            throw new RuntimeException("El proceso no puede continuar: El carrito está vacío");
        }

        // Paso B: Mapear los IDs de los productos usando nuestro CartItemDto
        // sincronizado
        List<Long> productIds = carrito.stream()
                .map(CartItemDto::getProductId)
                .collect(Collectors.toList());

        // Paso C: Crear la orden usando el Builder del modelo Order
        Order orden = Order.builder()
                .userId(userId)
                .status("PENDING")
                .totalAmount(calcularTotal(carrito))
                .productIds(productIds)
                .build();

        Order ordenGuardada = orderRepository.save(orden);
        log.info("Orden preliminar guardada con ID: {}. Procesando pago...", ordenGuardada.getId());

        // Paso D: Solicitar procesamiento de pago a ms-payment vía POST
        String respuestaPago = webClientBuilder.build()
                .post()
                .uri("http://ms-payment/api/payments/process/{orderId}", ordenGuardada.getId())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Respuesta recibida del sistema de pagos: {}", respuestaPago);

        // Paso E: Actualizar el estado final de la orden de acuerdo a los estados del
        // negocio
        if ("APPROVED".equalsIgnoreCase(respuestaPago)) {
            ordenGuardada.setStatus("PAID");
        } else {
            ordenGuardada.setStatus("CANCELED");
        }

        log.info("Checkout finalizado con éxito para el usuario: {}", userId);
        return orderRepository.save(ordenGuardada);
    }

    // 5. ACTUALIZAR ÓRDEN
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

    // 6. ELIMINAR ÓRDEN
    @Transactional
    public void deleteOrder(Long id) {
        log.info("Buscando orden ID: {} para eliminación", id);
        Order orden = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con el ID: " + id));

        orderRepository.delete(orden);
        log.info("Orden con ID: {} eliminada correctamente", id);
    }

    // === MÉTODO AUXILIAR REUTILIZABLE ===
    private Double calcularTotal(List<CartItemDto> items) {
        double total = 0;
        for (CartItemDto item : items) {
            total += item.getQuantity() * 10000; // Simulación de precio base ($10.000)
        }
        return total;
    }
}