package com.evaluation.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    private String id;
    private NotificationType type;
    private String message;
    private Instant timestamp;
    private boolean isRead;

    public static Notification create(NotificationType type, String message) {
        return Notification.builder()
                .id(UUID.randomUUID().toString())
                .type(type)
                .message(message)
                .timestamp(Instant.now())
                .isRead(false)
                .build();
    }
}
