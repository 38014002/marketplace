package com.marketplace.ms_search.ms_search.controller;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;
import com.marketplace.ms_search.ms_search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService service;

    @Test
    void buscar_debeRetornarResultados() throws Exception {
        // Given
        when(service.buscar("mouse")).thenReturn(List.of(
                new ProductResponseDto(1L, "Mouse", "Optico", 25.0)));

        // When / Then
        mockMvc.perform(get("/api/search").param("query", "mouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mouse"));
    }

    @Test
    void sincronizar_debeRetornar200() throws Exception {
        // Given
        doNothing().when(service).guardarProductoParaBusqueda(any());

        // When / Then
        mockMvc.perform(post("/api/search/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Teclado\",\"price\":40.0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Producto recibido y sincronizado correctamente"));
    }
}
