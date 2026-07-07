package com.marketplace.ms_catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.ms_catalog.dto.ApiResponse;
import com.marketplace.ms_catalog.dto.ProductRequestDTO;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.service.ProductService;
import com.marketplace.ms_catalog.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void obtenerTodos_debeRetornar200() throws Exception {
        // Given
        Product product = new Product();
        product.setName("Mouse");
        when(productService.listarTodos()).thenReturn(List.of(product));

        // When / Then
        mockMvc.perform(get("/api/catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mouse"));
    }

    @Test
    void crear_debeRetornar201() throws Exception {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Teclado");
        dto.setDescription("Mecanico");
        dto.setPrice(BigDecimal.valueOf(99));
        dto.setCategory("Accesorios");

        Product created = new Product();
        created.setId(1L);
        created.setName("Teclado");
        when(productService.crearProducto(any())).thenReturn(created);

        // When / Then
        mockMvc.perform(post("/api/catalog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Teclado"));
    }

    @Test
    void obtenerDetalle_debeRetornarApiResponse() throws Exception {
        // Given
        when(productService.obtenerProductoConStockCompleto(1L))
                .thenReturn(ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("ok")
                        .data(Map.of("id", 1, "name", "Mouse"))
                        .build());

        // When / Then
        mockMvc.perform(get("/api/catalog/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Mouse"));
    }

    @Test
    void actualizar_debeRetornar200() throws Exception {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Actualizado");
        dto.setDescription("desc");
        dto.setPrice(BigDecimal.valueOf(50));
        dto.setCategory("Cat");

        Product updated = new Product();
        updated.setId(1L);
        updated.setName("Actualizado");
        when(productService.actualizarProducto(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/catalog/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Actualizado"));
    }

    @Test
    void eliminar_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/catalog/1"))
                .andExpect(status().isNoContent());
    }
}
