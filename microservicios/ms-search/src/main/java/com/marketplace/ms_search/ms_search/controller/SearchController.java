package com.marketplace.ms_search.ms_search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.dto.SyncProductRequest;
import com.marketplace.ms_search.ms_search.model.SearchProduct;
import com.marketplace.ms_search.ms_search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Búsqueda", description = "Búsqueda de productos e índice sincronizado desde catálogo")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SearchController {

    private final SearchService service;

    @Operation(summary = "Buscar productos por nombre", description = "Búsqueda parcial case-insensitive")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda")
    @ApiResponse(responseCode = "400", description = "Query inválida")
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> buscar(
            @Parameter(description = "Texto a buscar en el nombre del producto", example = "mouse")
            @RequestParam @NotBlank(message = "query es obligatorio") String query) {
        log.info("GET /api/search?query={}", query);
        return ResponseEntity.ok(service.buscar(query));
    }

    @Operation(summary = "Sincronizar producto", description = "Recibe productos desde ms-catalog para indexar")
    @ApiResponse(responseCode = "200", description = "Producto sincronizado")
    @ApiResponse(responseCode = "400", description = "Datos de producto inválidos")
    @PostMapping("/sync")
    public ResponseEntity<String> sincronizar(@Valid @RequestBody SyncProductRequest request) {
        log.info("POST /api/search/sync - producto ID {}", request.getId());
        SearchProduct producto = SearchProduct.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
        service.guardarProductoParaBusqueda(producto);
        return ResponseEntity.ok("Producto recibido y sincronizado correctamente");
    }
}
