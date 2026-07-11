package com.marketplace.ms_order.controller;

import com.marketplace.ms_order.dto.ApiResponse;
import com.marketplace.ms_order.dto.OrderRequest;
import com.marketplace.ms_order.dto.OrderUpdateRequest;
import com.marketplace.ms_order.model.Order;
import com.marketplace.ms_order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Órdenes", description = "Endpoints para la creación, consulta, actualización y eliminación de órdenes de compra")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Crear una nueva orden manualmente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orden registrada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody OrderRequest request) {
        log.info("Petición para crear una nueva orden");
        Order order = Order.builder()
                .userId(request.getUserId())
                .productIds(request.getProductIds())
                .totalAmount(request.getTotalAmount())
                .build();
        Order nuevaOrden = orderService.saveOrder(order);
        return ResponseEntity.ok(ApiResponse.<Order>builder()
                .success(true)
                .message("Orden registrada con éxito")
                .data(nuevaOrden)
                .build());
    }

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

    @Operation(summary = "Actualizar una orden existente (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orden actualizada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateRequest request) {
        log.info("Petición para actualizar la orden con ID: {} - Operación protegida para ADMIN", id);

        Order orderDetails = Order.builder()
                .userId(request.getUserId())
                .status(request.getStatus())
                .totalAmount(request.getTotalAmount())
                .productIds(request.getProductIds())
                .build();
        Order ordenActualizada = orderService.updateOrder(id, orderDetails);

        return ResponseEntity.ok(ApiResponse.<Order>builder()
                .success(true)
                .message("Orden actualizada con éxito")
                .data(ordenActualizada)
                .build());
    }

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
