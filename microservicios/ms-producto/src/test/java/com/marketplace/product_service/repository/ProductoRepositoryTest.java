package com.marketplace.product_service.repository;

import com.marketplace.product_service.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false"
})
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void save_debePersistirProducto() {
        // Given
        Producto producto = Producto.builder()
                .name("Monitor")
                .description("27 pulgadas")
                .price(BigDecimal.valueOf(199.99))
                .stock(5)
                .category("Pantallas")
                .active(true)
                .build();

        // When
        Producto saved = productoRepository.save(producto);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Monitor", saved.getName());
    }

    @Test
    void findById_debeRetornarProducto() {
        // Given
        Producto saved = productoRepository.save(Producto.builder()
                .name("Auriculares")
                .price(BigDecimal.TEN)
                .stock(10)
                .active(true)
                .build());

        // When
        var result = productoRepository.findById(saved.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals("Auriculares", result.get().getName());
    }
}
