package com.marketplace.ms_cart.ms_cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.ms_cart.ms_cart.dto.CartDto;
import com.marketplace.ms_cart.ms_cart.model.Cart;
import com.marketplace.ms_cart.ms_cart.service.CartService;
import com.marketplace.ms_cart.ms_cart.security.JwtUtil;
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

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getCartByUser_debeRetornarItems() throws Exception {
        // Given
        when(cartService.getCartByUser(1L)).thenReturn(List.of(
                Cart.builder().id(1L).userId(1L).productId(10L).quantity(2).build()));

        // When / Then
        mockMvc.perform(get("/api/cart/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productId").value(10));
    }

    @Test
    void agregar_debeRetornar201() throws Exception {
        // Given
        CartDto dto = new CartDto(1L, 5L, 1);
        when(cartService.crear(any())).thenReturn(Cart.builder().id(1L).userId(1L).productId(5L).quantity(1).build());

        // When / Then
        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void eliminar_debeRetornar200() throws Exception {
        // Given
        doNothing().when(cartService).eliminar(3L);

        // When / Then
        mockMvc.perform(delete("/api/cart/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
