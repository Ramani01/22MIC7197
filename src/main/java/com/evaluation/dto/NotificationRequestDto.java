package com.evaluation.dto;

import com.evaluation.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDto {
    @NotNull(message = "Notification type cannot be null")
    private NotificationType type;

    @NotBlank(message = "Message cannot be blank")
    private String message;
}
