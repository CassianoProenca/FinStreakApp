package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Notification;

public interface SaveNotificationPort {
    Notification save(Notification notification);
}
