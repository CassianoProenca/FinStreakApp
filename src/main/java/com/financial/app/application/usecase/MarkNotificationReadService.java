package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.MarkNotificationReadUseCase;
import com.financial.app.application.ports.out.MarkNotificationReadPort;
import com.financial.app.domain.exception.UnauthorizedAccessException;
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
    public void execute(UUID userId, UUID notificationId) {
        if (!markNotificationReadPort.existsByIdAndUserId(notificationId, userId)) {
            throw new UnauthorizedAccessException("A notificação não pertence ao usuário autenticado");
        }
        markNotificationReadPort.markAsRead(notificationId);
    }
}
