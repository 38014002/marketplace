package com.marketplace.ms_notification.service;

import com.marketplace.ms_notification.dto.NotificationRequest;
import com.marketplace.ms_notification.model.Notification;
import com.marketplace.ms_notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    // Cambiamos el parámetro de Notification a NotificationRequest
    public Notification saveAndSend(NotificationRequest request) {
        log.info("Creando notificación para usuario ID: {} del tipo {}", request.getUserId(), request.getType());

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        // createdAt se setea solo en la entidad según tu código

        return repository.save(notification);
    }

    public List<Notification> getHistoryByUserId(Long userId) {
        log.debug("Buscando historial para el usuario {}", userId);
        return repository.findByUserId(userId);
    }
}