package com.marketplace.ms_notification.controller;

import com.marketplace.ms_notification.model.Notification;
import com.marketplace.ms_notification.service.NotificationService;
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
    public ResponseEntity<Notification> send(@RequestBody Notification notification) {
        return ResponseEntity.ok(service.saveAndSend(notification));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getHistoryByUserId(userId));
    }
}