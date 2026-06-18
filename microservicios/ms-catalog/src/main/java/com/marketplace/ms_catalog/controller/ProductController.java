package com.marketplace.ms_catalog.controller;

import com.marketplace.ms_catalog.dto.ApiResponse;
import com.marketplace.ms_catalog.dto.ProductRequestDTO;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // --- PÚBLICO: Cualquier usuario puede ver la lista ---
    @GetMapping
    public ResponseEntity<List<Product>> obtenerTodos() {
        return ResponseEntity.ok(productService.listarTodos());
    }

    // --- PÚBLICO: Cualquier usuario puede ver el detalle usando tu ApiResponse ---
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(productService.obtenerProductoConStockCompleto(id));
    }

    // --- RESTRINGIDO: Solo ADMIN puede crear usando el DTO validado ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> crear(@Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.crearProducto(dto));
    }

    // --- RESTRINGIDO: Solo ADMIN puede modificar usando el DTO validado ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> actualizar(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.actualizarProducto(id, dto));
    }

    // --- RESTRINGIDO: Solo ADMIN puede eliminar ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}