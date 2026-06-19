package com.marketplace.ms_notification.controller;

import com.marketplace.ms_notification.dto.ApiResponse;
import com.marketplace.ms_notification.dto.NotificationRequest;
import com.marketplace.ms_notification.model.Notification;
import com.marketplace.ms_notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notificaciones", description = "Envío y consulta de notificaciones a usuarios")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService service;

    @Operation(summary = "Enviar notificación", description = "Registra y procesa una notificación para un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notificación procesada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos")
    @PostMapping
    public ResponseEntity<ApiResponse<Notification>> send(@Valid @RequestBody NotificationRequest request) {
        log.info("Recibida petición de notificación para el usuario: {}", request.getUserId());
        Notification saved = service.saveAndSend(request);
        return ResponseEntity.ok(ApiResponse.success("Notificación procesada correctamente", saved));
    }

    @Operation(summary = "Historial por usuario", description = "Lista notificaciones enviadas a un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial obtenido")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getHistory(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long userId) {
        log.debug("Consultando historial para usuario ID: {}", userId);
        List<Notification> history = service.getHistoryByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Historial recuperado", history));
    }
}
