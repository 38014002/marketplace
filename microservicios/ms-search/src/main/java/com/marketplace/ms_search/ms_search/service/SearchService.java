package com.marketplace.ms_search.ms_search.service;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.model.SearchProduct;
import com.marketplace.ms_search.ms_search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository repository;

    // Guarda o actualiza el producto cuando el Catálogo avisa
    public void guardarProductoParaBusqueda(SearchProduct producto) {
        repository.save(producto);
    }

    // Busca y convierte el resultado al DTO de respuesta
    public List<ProductResponseDto> buscar(String query) {
        return repository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(p -> new ProductResponseDto(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice()))
                .collect(Collectors.toList());
    }
}