package com.marketplace.ms_search.ms_search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.model.SearchProduct;
import com.marketplace.ms_search.ms_search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Búsqueda", description = "Búsqueda de productos e índice sincronizado desde catálogo")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService service;

    @Operation(summary = "Buscar productos por nombre", description = "Búsqueda parcial case-insensitive")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda")
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> buscar(
            @Parameter(description = "Texto a buscar en el nombre del producto", example = "mouse")
            @RequestParam String query) {
        return ResponseEntity.ok(service.buscar(query));
    }

    @Operation(summary = "Sincronizar producto", description = "Recibe productos desde ms-catalog para indexar")
    @ApiResponse(responseCode = "200", description = "Producto sincronizado")
    @PostMapping("/sync")
    public ResponseEntity<String> sincronizar(@RequestBody SearchProduct producto) {
        service.guardarProductoParaBusqueda(producto);
        return ResponseEntity.ok("Producto recibido y sincronizado correctamente");
    }
}
