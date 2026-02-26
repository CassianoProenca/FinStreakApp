package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Notification;

import java.util.Optional;
import java.util.UUID;

public interface MarkNotificationReadPort {
    void markAsRead(UUID notificationId);
    void markAllAsRead(UUID userId);
    Optional<Notification> loadById(UUID notificationId);
    void deleteById(UUID notificationId);
}
