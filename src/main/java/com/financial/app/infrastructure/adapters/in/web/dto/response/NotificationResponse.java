package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String message,
        NotificationType type,
        boolean isRead,
        LocalDateTime createdAt
) {}
