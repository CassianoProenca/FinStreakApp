package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.Achievement;
import com.financial.app.infrastructure.adapters.out.persistence.entity.AchievementEntity;

public class AchievementMapper {
    public static Achievement toDomain(AchievementEntity entity) {
        if (entity == null) return null;
        return Achievement.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .name(entity.getName())
                .description(entity.getDescription())
                .earnedAt(entity.getEarnedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static AchievementEntity toEntity(Achievement domain) {
        if (domain == null) return null;
        AchievementEntity entity = AchievementEntity.builder()
                .userId(domain.getUserId())
                .type(domain.getType())
                .name(domain.getName())
                .description(domain.getDescription())
                .earnedAt(domain.getEarnedAt())
                .build();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }
}
