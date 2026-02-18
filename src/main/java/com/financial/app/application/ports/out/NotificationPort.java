package com.financial.app.application.ports.out;

import com.financial.app.domain.model.enums.NotificationType;

import java.util.UUID;

public interface NotificationPort {
    void notifyUser(UUID userId, String message, NotificationType type);
}
