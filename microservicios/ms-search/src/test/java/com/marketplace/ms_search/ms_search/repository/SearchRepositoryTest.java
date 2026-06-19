package com.marketplace.ms_search.ms_search.repository;

import com.marketplace.ms_search.ms_search.model.SearchProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SearchRepositoryTest {

    @Autowired
    private SearchRepository searchRepository;

    @Test
    void save_debePersistirProducto() {
        // Given
        SearchProduct product = SearchProduct.builder()
                .id(1L)
                .name("Teclado")
                .description("RGB")
                .price(49.99)
                .category("Accesorios")
                .build();

        // When
        SearchProduct saved = searchRepository.save(product);

        // Then
        assertEquals(1L, saved.getId());
        assertEquals("Teclado", saved.getName());
    }

    @Test
    void findByNameContainingIgnoreCase_debeBuscarParcial() {
        // Given
        searchRepository.save(SearchProduct.builder().id(1L).name("Mouse Gamer").price(25.0).build());
        searchRepository.save(SearchProduct.builder().id(2L).name("Teclado").price(40.0).build());

        // When
        var result = searchRepository.findByNameContainingIgnoreCase("mouse");

        // Then
        assertEquals(1, result.size());
        assertEquals("Mouse Gamer", result.get(0).getName());
    }
}
