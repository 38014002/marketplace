package com.marketplace.ms_notification.controller;

import com.marketplace.ms_notification.dto.NotificationRequest;
import com.marketplace.ms_notification.model.Notification;
import com.marketplace.ms_notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<Notification> send(@Valid @RequestBody NotificationRequest request) {
        log.info("Recibida petición de notificación para el usuario: {}", request.getUserId());
        // El service se encargará de convertir el DTO a Entity antes de guardar
        return ResponseEntity.ok(service.saveAndSend(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getHistory(@PathVariable Long userId) {
        log.debug("Consultando historial para usuario ID: {}", userId);
        return ResponseEntity.ok(service.getHistoryByUserId(userId));
    }
}