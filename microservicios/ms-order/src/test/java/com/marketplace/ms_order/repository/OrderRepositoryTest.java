package com.marketplace.ms_order.repository;

import com.marketplace.ms_order.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false"
})
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void save_debePersistirOrden() {
        // Given
        Order order = Order.builder()
                .userId(1L)
                .totalAmount(10000.0)
                .status("PENDING")
                .productIds(List.of(1L, 2L))
                .build();

        // When
        Order saved = orderRepository.save(order);

        // Then
        assertNotNull(saved.getId());
        assertEquals("PENDING", saved.getStatus());
    }

    @Test
    void findByUserId_debeRetornarOrdenesDelUsuario() {
        // Given
        orderRepository.save(Order.builder().userId(3L).totalAmount(50.0).status("PAID").build());
        orderRepository.save(Order.builder().userId(3L).totalAmount(80.0).status("PAID").build());
        orderRepository.save(Order.builder().userId(4L).totalAmount(10.0).status("PENDING").build());

        // When
        var result = orderRepository.findByUserId(3L);

        // Then
        assertEquals(2, result.size());
    }
}
