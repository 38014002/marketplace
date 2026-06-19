package com.marketplace.ms_order.controller;

import com.marketplace.ms_order.model.Order;
import com.marketplace.ms_order.service.OrderService;
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

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_debeRetornar200() throws Exception {
        // Given
        Order order = Order.builder().id(1L).userId(1L).status("PENDING").totalAmount(100.0).build();
        when(orderService.saveOrder(any())).thenReturn(order);

        // When / Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"totalAmount\":100.0,\"status\":\"PENDING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void getOrdersByUserId_debeRetornarOrdenes() throws Exception {
        // Given
        when(orderService.getOrdersByUser(2L)).thenReturn(List.of(
                Order.builder().id(1L).userId(2L).status("PAID").build()));

        // When / Then
        mockMvc.perform(get("/api/orders/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(2));
    }

    @Test
    void checkout_debeProcesarOrden() throws Exception {
        // Given
        when(orderService.checkout(5L)).thenReturn(
                Order.builder().id(10L).userId(5L).status("PAID").build());

        // When / Then
        mockMvc.perform(post("/api/orders/checkout/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
    }
}
