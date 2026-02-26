package com.financial.app.application.ports.in;

import java.util.UUID;

public interface MarkNotificationReadUseCase {
    void execute(UUID userId, UUID notificationId);
    void markAll(UUID userId);
    void delete(UUID userId, UUID notificationId);
}
