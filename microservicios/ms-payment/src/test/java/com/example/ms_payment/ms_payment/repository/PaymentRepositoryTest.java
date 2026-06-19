package com.example.ms_payment.ms_payment.repository;

import com.example.ms_payment.ms_payment.model.Payment;
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
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void save_debePersistirPago() {
        // Given
        Payment payment = Payment.builder()
                .orderId(1L)
                .amount(BigDecimal.valueOf(15000))
                .paymentMethod("CARD")
                .status("APPROVED")
                .build();

        // When
        Payment saved = paymentRepository.save(payment);

        // Then
        assertNotNull(saved.getId());
        assertEquals("APPROVED", saved.getStatus());
    }

    @Test
    void findByOrderId_debeRetornarPagosDeLaOrden() {
        // Given
        paymentRepository.save(Payment.builder().orderId(9L).amount(BigDecimal.TEN)
                .paymentMethod("CARD").status("APPROVED").build());
        paymentRepository.save(Payment.builder().orderId(9L).amount(BigDecimal.ONE)
                .paymentMethod("CASH").status("REJECTED").build());

        // When
        var result = paymentRepository.findByOrderId(9L);

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void findByStatus_debeFiltrarPorEstado() {
        // Given
        paymentRepository.save(Payment.builder().orderId(1L).amount(BigDecimal.TEN)
                .paymentMethod("CARD").status("APPROVED").build());
        paymentRepository.save(Payment.builder().orderId(2L).amount(BigDecimal.TEN)
                .paymentMethod("CARD").status("REJECTED").build());

        // When
        var result = paymentRepository.findByStatus("APPROVED");

        // Then
        assertEquals(1, result.size());
    }
}
