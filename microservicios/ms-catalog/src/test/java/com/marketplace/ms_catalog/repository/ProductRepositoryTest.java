package com.marketplace.ms_catalog.repository;

import com.marketplace.ms_catalog.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void save_debePersistirProducto() {
        // Given
        Product product = new Product();
        product.setName("Teclado");
        product.setDescription("Mecanico");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setCategory("Accesorios");

        // When
        Product saved = productRepository.save(product);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Teclado", saved.getName());
    }

    @Test
    void findAll_debeRetornarProductoGuardado() {
        // Given
        Product product = new Product();
        product.setName("Mouse");
        product.setDescription("Optico");
        product.setPrice(BigDecimal.TEN);
        product.setCategory("Accesorios");
        Product saved = productRepository.save(product);

        // When
        var result = productRepository.findById(saved.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals("Mouse", result.get().getName());
    }
}
