package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Notification;

import java.util.List;
import java.util.UUID;

public interface LoadNotificationsPort {
    List<Notification> loadByUserId(UUID userId);
}
