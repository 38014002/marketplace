package com.example.ms_payment.ms_payment.service;

import com.example.ms_payment.ms_payment.dto.PaymentDTO;
import com.example.ms_payment.ms_payment.exception.PaymentNotFoundException;
import com.example.ms_payment.ms_payment.model.Payment;
import com.example.ms_payment.ms_payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository repository;
    @InjectMocks private PaymentService paymentService;

    @Test
    void crear_conMontoValido_debeAprobarPago() {
        // Given
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderId(1L);
        dto.setAmount(new BigDecimal("5000"));
        dto.setPaymentMethod("Tarjeta");

        when(repository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        Payment result = paymentService.crear(dto);

        // Then
        assertEquals("APPROVED", result.getStatus());
    }

    @Test
    void crear_conMontoCero_debeLanzarExcepcion() {
        // Given
        PaymentDTO dto = new PaymentDTO();
        dto.setAmount(BigDecimal.ZERO);
        dto.setOrderId(1L);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> paymentService.crear(dto));
    }

    @Test
    void crear_conMontoAlto_debeRechazarPago() {
        // Given
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderId(2L);
        dto.setAmount(new BigDecimal("150000"));
        dto.setPaymentMethod("Transferencia");

        when(repository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Payment result = paymentService.crear(dto);

        // Then
        assertEquals("REJECTED", result.getStatus());
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(PaymentNotFoundException.class, () -> paymentService.buscarPorId(99L));
    }
}
