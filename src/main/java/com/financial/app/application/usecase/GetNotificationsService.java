package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.GetNotificationsUseCase;
import com.financial.app.application.ports.out.LoadNotificationsPort;
import com.financial.app.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNotificationsService implements GetNotificationsUseCase {

    private final LoadNotificationsPort loadNotificationsPort;

    @Override
    public List<Notification> execute(UUID userId) {
        return loadNotificationsPort.loadByUserId(userId);
    }
}
