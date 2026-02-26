package com.financial.app.infrastructure.adapters.out.notification;

import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveNotificationPort;
import com.financial.app.domain.model.Notification;
import com.financial.app.domain.model.User;
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
    private final LoadUserPort loadUserPort;

    @Override
    public void notifyUser(UUID userId, String message, NotificationType type) {
        // Respect the user's notificationsEnabled preference (#8)
        User user = loadUserPort.loadById(userId).orElse(null);
        if (user != null && user.getPreferences() != null && !user.getPreferences().isNotificationsEnabled()) {
            log.debug("Notifications disabled for user {}, skipping [{}]", userId, type);
            return;
        }

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
