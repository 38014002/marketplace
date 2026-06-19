package com.marketplace.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.product_service.dto.ProductoDto;
import com.marketplace.product_service.dto.ProductoResponse;
import com.marketplace.product_service.service.ProductoService;
import com.marketplace.product_service.security.JwtUtil;
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

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService service;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void listar_debeRetornar200() throws Exception {
        // Given
        when(service.listarTodos()).thenReturn(List.of(
                ProductoResponse.builder().id(1L).name("Mouse").stock(5).available(true).build()));

        // When / Then
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mouse"));
    }

    @Test
    void obtener_debeRetornarProducto() throws Exception {
        // Given
        when(service.buscarPorId(1L)).thenReturn(
                ProductoResponse.builder().id(1L).name("Teclado").stock(3).available(true).build());

        // When / Then
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teclado"));
    }

    @Test
    void crear_debeRetornar201() throws Exception {
        // Given
        ProductoDto dto = ProductoDto.builder()
                .name("Monitor")
                .description("27 pulgadas")
                .price(BigDecimal.valueOf(199))
                .category("Pantallas")
                .stock(2)
                .build();
        when(service.crear(any())).thenReturn(
                ProductoResponse.builder().id(1L).name("Monitor").stock(2).available(true).build());

        // When / Then
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Monitor"));
    }

    @Test
    void actualizar_debeRetornar200() throws Exception {
        ProductoDto dto = ProductoDto.builder()
                .name("Actualizado")
                .description("desc")
                .price(BigDecimal.valueOf(150))
                .category("Cat")
                .stock(1)
                .build();
        when(service.actualizar(eq(1L), any())).thenReturn(
                ProductoResponse.builder().id(1L).name("Actualizado").stock(1).available(true).build());

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Actualizado"));
    }

    @Test
    void eliminar_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}
