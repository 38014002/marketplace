package com.marketplace.ms_order.controller;

import com.marketplace.ms_order.dto.ApiResponse;
import com.marketplace.ms_order.model.Order;
import com.marketplace.ms_order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Órdenes", description = "Endpoints para la creación, consulta, actualización y eliminación de órdenes de compra")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    // 1. CREAR ÓRDEN - ACCESO: USUARIOS AUTENTICADOS
    @Operation(summary = "Crear una nueva orden manualmente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orden registrada con éxito")
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody Order order) {
        log.info("Petición para crear una nueva orden");
        Order nuevaOrden = orderService.saveOrder(order);
        return ResponseEntity.ok(ApiResponse.<Order>builder()
                .success(true)
                .message("Orden registrada con éxito")
                .data(nuevaOrden)
                .build());
    }

    // 2. LISTAR TODAS LAS ÓRDENES - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Listar todas las órdenes del sistema (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de órdenes obtenido correctamente")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        log.info("Petición global de órdenes - Operación protegida para ADMIN");
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.<List<Order>>builder()
                .success(true)
                .message("Listado de órdenes obtenido correctamente")
                .data(orders)
                .build());
    }

    // 3. OBTENER ÓRDENES POR USUARIO - ACCESO: USUARIOS AUTENTICADOS
    @Operation(summary = "Obtener el historial de órdenes de un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Órdenes del usuario obtenidas correctamente")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByUserId(@PathVariable Long userId) {
        log.info("Petición de órdenes para el usuario con ID: {}", userId);
        List<Order> userOrders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(ApiResponse.<List<Order>>builder()
                .success(true)
                .message("Órdenes del usuario obtenidas correctamente")
                .data(userOrders)
                .build());
    }

    // 4. PROCESAR CHECKOUT - ACCESO: USUARIOS AUTENTICADOS
    @Operation(summary = "Procesar el checkout del carrito actual de un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Checkout procesado y orden generada")
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<ApiResponse<Order>> checkout(@PathVariable Long userId) {
        log.info("Iniciando proceso de checkout para el usuario con ID: {}", userId);
        Order ordenProcesada = orderService.checkout(userId);
        return ResponseEntity.ok(ApiResponse.<Order>builder()
                .success(true)
                .message("Checkout procesado y orden generada correctamente")
                .data(ordenProcesada)
                .build());
    }

    // 5. ACTUALIZAR ÓRDEN - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Actualizar una orden existente (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orden actualizada con éxito")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> updateOrder(
            @PathVariable Long id,
            @RequestBody Order orderDetails) {
        log.info("Petición para actualizar la orden con ID: {} - Operación protegida para ADMIN", id);

        Order ordenActualizada = orderService.updateOrder(id, orderDetails);

        return ResponseEntity.ok(ApiResponse.<Order>builder()
                .success(true)
                .message("Orden actualizada con éxito")
                .data(ordenActualizada)
                .build());
    }

    // 6. ELIMINAR ÓRDEN - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Eliminar una orden por su ID (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orden eliminada con éxito")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        log.info("Petición para eliminar la orden con ID: {} - Operación protegida para ADMIN", id);

        orderService.deleteOrder(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Orden eliminada correctamente")
                .build());
    }
}