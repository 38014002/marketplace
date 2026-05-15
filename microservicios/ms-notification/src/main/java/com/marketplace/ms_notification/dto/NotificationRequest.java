package com.marketplace.ms_notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long userId;

    @NotBlank(message = "El mensaje no puede estar vacío")
    private String message;

    @NotBlank(message = "El tipo (EMAIL/SMS) es obligatorio")
    private String type;
}