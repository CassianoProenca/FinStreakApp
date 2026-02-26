package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadNotificationsPort;
import com.financial.app.application.ports.out.MarkNotificationReadPort;
import com.financial.app.application.ports.out.SaveNotificationPort;
import com.financial.app.domain.model.Notification;
import com.financial.app.infrastructure.adapters.out.persistence.entity.NotificationEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.NotificationMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaNotificationAdapter implements SaveNotificationPort, LoadNotificationsPort, MarkNotificationReadPort {

    private final NotificationJpaRepository repository;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = NotificationMapper.toEntity(notification);
        return NotificationMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Notification> loadByUserId(UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(UUID notificationId) {
        repository.findById(notificationId).ifPresent(entity -> {
            entity.setRead(true);
            repository.save(entity);
        });
    }

    @Override
    public void markAllAsRead(UUID userId) {
        List<NotificationEntity> unread = repository.findByUserIdAndIsReadFalse(userId);
        unread.forEach(e -> e.setRead(true));
        repository.saveAll(unread);
    }

    @Override
    public Optional<Notification> loadById(UUID notificationId) {
        return repository.findById(notificationId).map(NotificationMapper::toDomain);
    }

    @Override
    public void deleteById(UUID notificationId) {
        repository.deleteById(notificationId);
    }

    public long countUnread(UUID userId) {
        return repository.countByUserIdAndIsReadFalse(userId);
    }
}
