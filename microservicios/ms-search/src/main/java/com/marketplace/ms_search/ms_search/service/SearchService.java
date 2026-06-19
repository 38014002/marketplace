package com.marketplace.ms_search.ms_search.service;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.model.SearchProduct;
import com.marketplace.ms_search.ms_search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository repository;

    public void guardarProductoParaBusqueda(SearchProduct producto) {
        log.info("Sincronizando producto ID {} para búsqueda", producto.getId());
        repository.save(producto);
    }

    public List<ProductResponseDto> buscar(String query) {
        log.info("Buscando productos con query '{}'", query);
        List<ProductResponseDto> resultados = repository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(p -> new ProductResponseDto(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice()))
                .collect(Collectors.toList());
        log.debug("Se encontraron {} resultados", resultados.size());
        return resultados;
    }
}
