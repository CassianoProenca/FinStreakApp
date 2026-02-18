package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.PasswordResetToken;
import com.financial.app.infrastructure.adapters.out.persistence.entity.PasswordResetTokenEntity;

public class PasswordResetTokenMapper {

    public static PasswordResetToken toDomain(PasswordResetTokenEntity entity) {
        if (entity == null) return null;
        return PasswordResetToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .used(entity.isUsed())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static PasswordResetTokenEntity toEntity(PasswordResetToken domain) {
        if (domain == null) return null;
        PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder()
                .userId(domain.getUserId())
                .token(domain.getToken())
                .expiresAt(domain.getExpiresAt())
                .used(domain.isUsed())
                .build();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }
}
