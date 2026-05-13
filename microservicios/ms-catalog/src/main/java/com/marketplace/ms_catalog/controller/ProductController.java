package com.marketplace.ms_catalog.controller;

import com.marketplace.ms_catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerDetalle(@PathVariable Long id) {
        // El controlador ahora es súper limpio porque el Service hace el trabajo pesado
        return ResponseEntity.ok(productService.obtenerProductoConStockCompleto(id));
    }
}