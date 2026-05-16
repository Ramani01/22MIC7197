package com.evaluation.service;

import com.evaluation.dto.NotificationRequestDto;
import com.evaluation.exception.ResourceNotFoundException;
import com.evaluation.model.LogLevel;
import com.evaluation.model.LogPackage;
import com.evaluation.model.LogStack;
import com.evaluation.model.Notification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final Map<String, Notification> notificationMap = new ConcurrentHashMap<>();
    private final LoggingService loggingService;

    public NotificationService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public List<Notification> getAllNotifications() {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.service, "Fetching all notifications");
        return new ArrayList<>(notificationMap.values());
    }

    public Notification createNotification(NotificationRequestDto requestDto) {
        Notification notification = Notification.create(requestDto.getType(), requestDto.getMessage());
        notificationMap.put(notification.getId(), notification);
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.service, "Created notification: " + notification.getId());
        return notification;
    }

    public Notification markAsRead(String id) {
        Notification notification = notificationMap.get(id);
        if (notification == null) {
            loggingService.log(LogStack.backend, LogLevel.error, LogPackage.service, "Notification not found: " + id);
            throw new ResourceNotFoundException("Notification with id " + id + " not found");
        }
        notification.setRead(true);
        notificationMap.put(id, notification);
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.service, "Marked notification as read: " + id);
        return notification;
    }

    public void deleteNotification(String id) {
        Notification removed = notificationMap.remove(id);
        if (removed == null) {
            loggingService.log(LogStack.backend, LogLevel.warn, LogPackage.service, "Attempted to delete non-existent notification: " + id);
            throw new ResourceNotFoundException("Notification with id " + id + " not found");
        }
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.service, "Deleted notification: " + id);
    }

    public List<Notification> getTopNotifications() {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.service, "Fetching top 10 priority notifications");

        return notificationMap.values().stream()
                .sorted(Comparator
                        .comparingInt((Notification n) -> getPriority(n))
                        .thenComparing(Notification::getTimestamp).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private int getPriority(Notification notification) {
        switch (notification.getType()) {
            case Placement: return 1;
            case Result: return 2;
            case Event: return 3;
            default: return 99;
        }
    }
}
