package com.marketplace.ms_search.ms_search.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.marketplace.ms_search.ms_search.client.ProductClient;
import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final ProductClient productClient;

    public List<ProductResponseDto> buscar(String query) {
    log.info("Buscando productos {}", query);
    return productClient.buscarPorNombre(query);
    }
}