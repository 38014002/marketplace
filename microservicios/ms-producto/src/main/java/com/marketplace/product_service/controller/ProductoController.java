package com.marketplace.product_service.controller;

import com.marketplace.product_service.dto.ProductoDto;
import com.marketplace.product_service.dto.ProductoResponse;
import com.marketplace.product_service.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Productos", description = "Fachada que agrega catálogo e inventario en una sola API")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductoController {

    private final ProductoService service;

    @Operation(summary = "Listar productos", description = "Productos con stock y disponibilidad")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado obtenido")
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Obtener producto por ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Crear producto", description = "Solo ADMIN. Orquesta ms-catalog y ms-inventory")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoDto dto) {
        log.info("POST /api/productos - crear {}", dto.getName());
        ProductoResponse creado = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar producto", description = "Solo ADMIN")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponse> actualizar(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @Valid @RequestBody ProductoDto dto) {
        log.info("PUT /api/productos/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar producto", description = "Solo ADMIN. Elimina en catálogo e inventario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Producto eliminado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        log.info("DELETE /api/productos/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
