package com.financial.app.application.ports.out;

import java.util.UUID;

public interface NotificationPort {
    void notifyUser(UUID userId, String message);
}
