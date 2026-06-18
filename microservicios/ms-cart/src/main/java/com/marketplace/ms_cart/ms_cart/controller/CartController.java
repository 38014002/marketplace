package com.marketplace.ms_cart.ms_cart.controller;

import com.marketplace.ms_cart.ms_cart.dto.CartDto;
import com.marketplace.ms_cart.ms_cart.dto.ApiResponse;
import com.marketplace.ms_cart.ms_cart.model.Cart;
import com.marketplace.ms_cart.ms_cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Carrito", description = "Endpoints para la gestión de ítems en el carrito de compras")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    // 1. OBTENER ÍTEMS DEL CARRITO POR USUARIO (Usado internamente por ms-order y
    // por el cliente)
    @Operation(summary = "Obtener todos los ítems del carrito de un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Items del carrito obtenidos con éxito")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Cart>>> getCartByUser(@PathVariable Long userId) {
        log.info("Petición recibida para obtener el carrito del usuario con ID: {}", userId);

        List<Cart> items = cartService.getCartByUser(userId);

        return ResponseEntity.ok(ApiResponse.<List<Cart>>builder()
                .success(true)
                .message("Items del carrito obtenidos con éxito")
                .data(items)
                .build());
    }

    // 2. AGREGAR ÍTEM AL CARRITO
    @Operation(summary = "Agregar un producto al carrito de un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Item agregado al carrito")
    @PostMapping
    public ResponseEntity<ApiResponse<Cart>> agregar(@Valid @RequestBody CartDto dto) {
        log.info("Agregando producto {} al carrito del usuario {}", dto.getProductId(), dto.getUserId());

        Cart item = cartService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Cart>builder()
                .success(true)
                .message("Producto agregado al carrito")
                .data(item)
                .build());
    }

    // 3. LISTAR TODOS LOS CARRITOS REGISTRADOS - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Listar todos los ítems de carritos del sistema (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado global obtenido correctamente")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Cart>>> listarTodos() {
        log.info("Petición global de carritos - Operación protegida para ADMIN");

        List<Cart> todos = cartService.listarTodos();

        return ResponseEntity.ok(ApiResponse.<List<Cart>>builder()
                .success(true)
                .message("Listado global de carritos obtenido correctamente")
                .data(todos)
                .build());
    }

    // 3. ELIMINAR O VACIAR UN ÍTEM DEL CARRITO
    @Operation(summary = "Eliminar un ítem específico del carrito por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item eliminado con éxito")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        log.info("Petición para eliminar el ítem de carrito con ID: {}", id);

        cartService.eliminar(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Artículo removido del carrito correctamente")
                .build());
    }
}