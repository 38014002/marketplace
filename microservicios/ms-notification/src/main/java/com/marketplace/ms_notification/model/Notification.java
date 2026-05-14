package com.marketplace.ms_notification.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String message;
    private String type; // Ejemplo: ORDER_CONFIRMED, WELCOME, PAYMENT_ERROR

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}