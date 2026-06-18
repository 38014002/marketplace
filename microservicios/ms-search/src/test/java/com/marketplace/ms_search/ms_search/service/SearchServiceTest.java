package com.marketplace.ms_search.ms_search.service;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.model.SearchProduct;
import com.marketplace.ms_search.ms_search.repository.SearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock private SearchRepository repository;
    @InjectMocks private SearchService searchService;

    @Test
    void buscar_debeMapearResultados() {
        // Given
        SearchProduct product = SearchProduct.builder()
                .id(1L).name("Mouse").description("Inalambrico").price(25.5).build();
        when(repository.findByNameContainingIgnoreCase("mouse")).thenReturn(List.of(product));

        // When
        List<ProductResponseDto> result = searchService.buscar("mouse");

        // Then
        assertEquals(1, result.size());
        assertEquals("Mouse", result.get(0).getName());
    }

    @Test
    void guardarProductoParaBusqueda_debePersistir() {
        // Given
        SearchProduct product = new SearchProduct();

        // When
        searchService.guardarProductoParaBusqueda(product);

        // Then
        verify(repository).save(product);
    }
}
