package com.marketplace.ms_notification.service;

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

    public Notification saveAndSend(Notification notification) {
        log.info("Enviando notificación tipo {} al usuario {}: {}",
                notification.getType(), notification.getUserId(), notification.getMessage());
        return repository.save(notification);
    }

    public List<Notification> getHistoryByUserId(Long userId) {
        return repository.findByUserId(userId);
    }
}