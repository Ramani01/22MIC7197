package com.evaluation.controller;

import com.evaluation.dto.NotificationRequestDto;
import com.evaluation.model.LogLevel;
import com.evaluation.model.LogPackage;
import com.evaluation.model.LogStack;
import com.evaluation.model.Notification;
import com.evaluation.service.LoggingService;
import com.evaluation.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final LoggingService loggingService;

    public NotificationController(NotificationService notificationService, LoggingService loggingService) {
        this.notificationService = notificationService;
        this.loggingService = loggingService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.controller, "GET /notifications called");
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody NotificationRequestDto dto) {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.controller, "POST /notifications called");
        return new ResponseEntity<>(notificationService.createNotification(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.controller, "PATCH /notifications/" + id + "/read called");
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.controller, "DELETE /notifications/" + id + " called");
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public ResponseEntity<List<Notification>> getTopNotifications() {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.controller, "GET /notifications/top called");
        return ResponseEntity.ok(notificationService.getTopNotifications());
    }
}
