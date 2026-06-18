package com.marketplace.ms_notification.service;

import com.marketplace.ms_notification.dto.NotificationRequest;
import com.marketplace.ms_notification.model.Notification;
import com.marketplace.ms_notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository repository;
    @InjectMocks private NotificationService notificationService;

    @Test
    void saveAndSend_debeCrearNotificacion() {
        // Given
        NotificationRequest request = new NotificationRequest();
        request.setUserId(1L);
        request.setType("ORDER");
        request.setMessage("Orden pagada");

        when(repository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(1L);
            return n;
        });

        // When
        Notification result = notificationService.saveAndSend(request);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("ORDER", result.getType());
    }

    @Test
    void getHistoryByUserId_debeRetornarHistorial() {
        // Given
        when(repository.findByUserId(1L)).thenReturn(List.of(new Notification()));

        // When
        List<Notification> history = notificationService.getHistoryByUserId(1L);

        // Then
        assertEquals(1, history.size());
    }
}
