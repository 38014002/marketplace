package com.marketplace.ms_inventory.controller;

import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.service.InventoryService;
import com.marketplace.ms_inventory.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void obtenerInventarioGeneral_debeRetornar200() throws Exception {
        // Given
        when(inventoryService.listarTodo()).thenReturn(List.of(
                Inventory.builder().productId(1).stock(10).build()));

        // When / Then
        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].stock").value(10));
    }

    @Test
    void obtenerStockPorProducto_debeRetornarStock() throws Exception {
        // Given
        when(inventoryService.consultarStock(1)).thenReturn(15);

        // When / Then
        mockMvc.perform(get("/api/v1/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(15));
    }

    @Test
    void crearInventario_debeRetornar201() throws Exception {
        // Given
        when(inventoryService.crearInventario(any())).thenReturn(
                Inventory.builder().id(1L).productId(2).stock(5).build());

        // When / Then
        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":2,\"stock\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.productId").value(2));
    }
}
