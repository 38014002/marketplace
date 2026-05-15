package com.marketplace.ms_catalog.controller;

import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // --- NUEVO: ENDPOINT PARA EL CATÁLOGO (Vista General) ---
    @GetMapping
    public ResponseEntity<List<Product>> obtenerTodos() {
        // Llama al service para traer la lista de la DB de Catalog
        return ResponseEntity.ok(productService.listarTodos());
    }

    // --- EL QUE YA TENÍAS: DETALLE DEL PRODUCTO ---
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(productService.obtenerProductoConStockCompleto(id));
    }
    // ... otros métodos (GET)

    @PostMapping // <-- Faltaba este endpoint
    public ResponseEntity<Product> crear(@RequestBody Product producto) {
        // Al llamar a crearProducto, se dispara automáticamente la sincronización con
        // Search
        return ResponseEntity.status(201).body(productService.crearProducto(producto));
    }
}