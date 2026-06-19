package com.marketplace.ms_notification.repository;

import com.marketplace.ms_notification.model.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void save_debePersistirNotificacion() {
        // Given
        Notification notification = new Notification();
        notification.setUserId(1L);
        notification.setMessage("Bienvenido");
        notification.setType("WELCOME");

        // When
        Notification saved = notificationRepository.save(notification);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Bienvenido", saved.getMessage());
    }

    @Test
    void findByUserId_debeRetornarHistorial() {
        // Given
        Notification n1 = new Notification();
        n1.setUserId(5L);
        n1.setMessage("Orden confirmada");
        n1.setType("ORDER_CONFIRMED");
        notificationRepository.save(n1);

        Notification n2 = new Notification();
        n2.setUserId(5L);
        n2.setMessage("Pago recibido");
        n2.setType("PAYMENT");
        notificationRepository.save(n2);

        // When
        var result = notificationRepository.findByUserId(5L);

        // Then
        assertEquals(2, result.size());
    }
}
