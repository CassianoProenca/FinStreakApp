package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.infrastructure.adapters.out.persistence.entity.GamificationProfileEntity;

public class GamificationMapper {

    public static GamificationProfile toDomain(GamificationProfileEntity entity) {
        if (entity == null) {
            return null;
        }

        return GamificationProfile.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .currentStreak(entity.getCurrentStreak())
                .maxStreak(entity.getMaxStreak())
                .totalXp(entity.getTotalXp())
                .lastActivityDate(entity.getLastActivityDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static GamificationProfileEntity toEntity(GamificationProfile domain) {
        if (domain == null) {
            return null;
        }

        GamificationProfileEntity entity = GamificationProfileEntity.builder()
                .userId(domain.getUserId())
                .currentStreak(domain.getCurrentStreak())
                .maxStreak(domain.getMaxStreak())
                .totalXp(domain.getTotalXp())
                .lastActivityDate(domain.getLastActivityDate())
                .build();

        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion() != null ? domain.getVersion() : 0L);

        return entity;
    }
}
