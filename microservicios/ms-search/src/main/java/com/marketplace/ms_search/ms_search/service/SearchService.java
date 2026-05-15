package com.marketplace.ms_search.ms_search.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.model.SearchProduct;
import com.marketplace.ms_search.ms_search.repository.SearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository repository;

    /**
     * Busca en la base de datos local y convierte el resultado a DTO
     */
    public List<ProductResponseDto> buscar(String query) {
        log.info("Buscando productos localmente con la query: {}", query);

        return repository.findByNombreContainingIgnoreCase(query)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Guarda o actualiza el producto cuando el MS-PRODUCTO avisa
     */
    public void guardarProductoParaBusqueda(SearchProduct product) {
        repository.save(product);
        log.info("✅ Producto '{}' guardado en el índice de búsqueda.", product.getNombre());
    }

    private ProductResponseDto convertToDto(SearchProduct product) {
        return ProductResponseDto.builder()

                .name(product.getNombre()) // .name() porque así está en el DTO
                .description(product.getDescripcion()) // .description() en el DTO
                .price(java.math.BigDecimal.valueOf(product.getPrecio())) // Conversión de Double a BigDecimal
                .category(product.getCategoria()) // .category() en el DTO
                .build();
    }
}