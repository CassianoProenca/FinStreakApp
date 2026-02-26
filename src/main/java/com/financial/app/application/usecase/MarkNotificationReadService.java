package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.MarkNotificationReadUseCase;
import com.financial.app.application.ports.out.MarkNotificationReadPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.exception.UnauthorizedAccessException;
import com.financial.app.domain.model.Notification;
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
        Notification notification = markNotificationReadPort.loadById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        if (!notification.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para acessar esta notificação");
        }

        markNotificationReadPort.markAsRead(notificationId);
    }

    @Override
    public void markAll(UUID userId) {
        markNotificationReadPort.markAllAsRead(userId);
    }

    @Override
    public void delete(UUID userId, UUID notificationId) {
        Notification notification = markNotificationReadPort.loadById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        if (!notification.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para excluir esta notificação");
        }

        markNotificationReadPort.deleteById(notificationId);
    }
}
