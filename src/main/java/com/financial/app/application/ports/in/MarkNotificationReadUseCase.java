package com.financial.app.application.ports.in;

import java.util.UUID;

public interface MarkNotificationReadUseCase {
    void execute(UUID notificationId);
}
