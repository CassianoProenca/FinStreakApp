package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.MarkNotificationReadUseCase;
import com.financial.app.application.ports.out.MarkNotificationReadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkNotificationReadService implements MarkNotificationReadUseCase {

    private final MarkNotificationReadPort markNotificationReadPort;

    @Override
    public void execute(UUID notificationId) {
        markNotificationReadPort.markAsRead(notificationId);
    }
}
