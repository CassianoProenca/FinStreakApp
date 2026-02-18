package com.financial.app.application.ports.in;

import com.financial.app.domain.model.Notification;

import java.util.List;
import java.util.UUID;

public interface GetNotificationsUseCase {
    List<Notification> execute(UUID userId);
}
