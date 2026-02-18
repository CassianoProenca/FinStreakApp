package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.Notification;
import com.financial.app.infrastructure.adapters.out.persistence.entity.NotificationEntity;

public class NotificationMapper {

    public static Notification toDomain(NotificationEntity entity) {
        if (entity == null) return null;
        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .message(entity.getMessage())
                .type(entity.getType())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static NotificationEntity toEntity(Notification domain) {
        if (domain == null) return null;
        NotificationEntity entity = NotificationEntity.builder()
                .userId(domain.getUserId())
                .message(domain.getMessage())
                .type(domain.getType())
                .isRead(domain.isRead())
                .build();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }
}
