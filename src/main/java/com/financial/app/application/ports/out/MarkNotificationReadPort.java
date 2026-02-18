package com.financial.app.application.ports.out;

import java.util.UUID;

public interface MarkNotificationReadPort {
    void markAsRead(UUID notificationId);
}
