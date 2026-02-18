package com.financial.app.infrastructure.adapters.out.notification;

import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveNotificationPort;
import com.financial.app.domain.model.Notification;
import com.financial.app.domain.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PersistentNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(PersistentNotificationAdapter.class);

    private final SaveNotificationPort saveNotificationPort;

    @Override
    public void notifyUser(UUID userId, String message, NotificationType type) {
        log.info("NOTIFICAÇÃO [{}] PARA USER {}: {}", type, userId, message);

        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        saveNotificationPort.save(notification);
    }
}
