package com.marketplace.ms_notification.controller;

import com.marketplace.ms_notification.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<Notification>> send(@Valid @RequestBody NotificationRequest request) {
        log.info("Recibida petición de notificación para el usuario: {}", request.getUserId());
        Notification saved = service.saveAndSend(request);
        // Usamos el ApiResponse que creamos para que el JSON sea más profesional
        return ResponseEntity.ok(ApiResponse.success("Notificación procesada correctamente", saved));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getHistory(@PathVariable Long userId) {
        log.debug("Consultando historial para usuario ID: {}", userId);
        List<Notification> history = service.getHistoryByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Historial recuperado", history));
    }
}