package com.financial.app.infrastructure.adapters.out.notification;

import com.financial.app.application.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LogNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LogNotificationAdapter.class);

    @Override
    public void notifyUser(UUID userId, String message) {
        log.info("NOTIFICAÇÃO PARA USER {}: {}", userId, message);
    }
}
