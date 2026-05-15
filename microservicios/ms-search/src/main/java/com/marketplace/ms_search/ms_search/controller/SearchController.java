package com.marketplace.ms_search.ms_search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.model.SearchProduct; // Asegúrate de que este import exista
import com.marketplace.ms_search.ms_search.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService service;

    // Este es el que ya tenías para buscar productos
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> buscar(@RequestParam String query) {
        return ResponseEntity.ok(service.buscar(query));
    }

    // AGREGA ESTO: Este es el endpoint que falta para recibir datos de Postman o
    // del MS de Productos
    @PostMapping("/sync")
    public ResponseEntity<String> sincronizar(@RequestBody SearchProduct producto) {
        service.guardarProductoParaBusqueda(producto);
        return ResponseEntity.ok("✅ Producto recibido y sincronizado correctamente");
    }

}