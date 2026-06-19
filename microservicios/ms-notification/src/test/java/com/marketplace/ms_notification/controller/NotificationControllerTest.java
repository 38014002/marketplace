package com.marketplace.ms_notification.controller;

import com.marketplace.ms_notification.model.Notification;
import com.marketplace.ms_notification.service.NotificationService;
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

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService service;

    @Test
    void send_debeRetornar200() throws Exception {
        // Given
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUserId(1L);
        notification.setMessage("Hola");
        notification.setType("WELCOME");
        when(service.saveAndSend(any())).thenReturn(notification);

        // When / Then
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"message\":\"Hola\",\"type\":\"EMAIL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getHistory_debeRetornarHistorial() throws Exception {
        // Given
        Notification n = new Notification();
        n.setId(1L);
        n.setUserId(3L);
        n.setMessage("Orden OK");
        when(service.getHistoryByUserId(3L)).thenReturn(List.of(n));

        // When / Then
        mockMvc.perform(get("/api/notifications/user/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].message").value("Orden OK"));
    }
}
