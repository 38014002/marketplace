package com.marketplace.ms_catalog.controller;

import com.marketplace.ms_catalog.dto.ApiResponse;
import com.marketplace.ms_catalog.dto.ProductRequestDTO;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.service.ProductService;
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
import java.util.Map;

@Tag(name = "Catálogo", description = "Gestión del catálogo de productos y consulta con stock")
@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Listar productos", description = "Devuelve todos los productos del catálogo")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado obtenido")
    @GetMapping
    public ResponseEntity<List<Product>> obtenerTodos() {
        return ResponseEntity.ok(productService.listarTodos());
    }

    @Operation(summary = "Detalle de producto con stock", description = "Incluye información de inventario vía ms-inventory")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerDetalle(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productService.obtenerProductoConStockCompleto(id));
    }

    @Operation(summary = "Crear producto", description = "Solo ADMIN. Sincroniza con ms-search")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> crear(@Valid @RequestBody ProductRequestDTO dto) {
        log.info("POST /api/v1/catalog - crear producto {}", dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.crearProducto(dto));
    }

    @Operation(summary = "Actualizar producto", description = "Solo ADMIN")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> actualizar(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {
        log.info("PUT /api/v1/catalog/{} - actualizar producto", id);
        return ResponseEntity.ok(productService.actualizarProducto(id, dto));
    }

    @Operation(summary = "Eliminar producto", description = "Solo ADMIN")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Producto eliminado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        log.info("DELETE /api/v1/catalog/{} - eliminar producto", id);
        productService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
