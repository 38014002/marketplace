package com.example.ms_payment.ms_payment.controller;

import com.example.ms_payment.ms_payment.model.Payment;
import com.example.ms_payment.ms_payment.service.PaymentService;
import com.example.ms_payment.ms_payment.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService service;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void listar_debeRetornar200() throws Exception {
        // Given
        when(service.listarTodos()).thenReturn(List.of(
                Payment.builder().id(1L).orderId(1L).status("APPROVED").build()));

        // When / Then
        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void crear_debeRetornar201() throws Exception {
        // Given
        when(service.crear(any())).thenReturn(Payment.builder()
                .id(1L).orderId(5L).amount(BigDecimal.TEN).status("APPROVED").build());

        // When / Then
        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":5,\"amount\":10000,\"paymentMethod\":\"CARD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(5));
    }

    @Test
    void processPayment_debeRetornarApproved() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/pagos/process/7"))
                .andExpect(status().isOk())
                .andExpect(content().string("APPROVED"));
    }

    @Test
    void obtenerPorId_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Payment.builder()
                .id(1L).orderId(5L).status("APPROVED").build());

        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void actualizar_debeRetornar200() throws Exception {
        when(service.actualizar(eq(1L), any())).thenReturn(Payment.builder()
                .id(1L).orderId(5L).amount(BigDecimal.TEN).status("APPROVED").build());

        mockMvc.perform(put("/api/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":5,\"amount\":10,\"paymentMethod\":\"CARD\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(5));
    }

    @Test
    void eliminar_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/pagos/1"))
                .andExpect(status().isNoContent());
    }
}
