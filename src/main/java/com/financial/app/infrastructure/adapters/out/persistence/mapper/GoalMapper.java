package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.Goal;
import com.financial.app.infrastructure.adapters.out.persistence.entity.GoalEntity;

public class GoalMapper {

    public static Goal toDomain(GoalEntity entity) {
        if (entity == null) {
            return null;
        }

        return Goal.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .targetAmount(entity.getTargetAmount())
                .currentAmount(entity.getCurrentAmount())
                .deadline(entity.getDeadline())
                .status(entity.getStatus())
                .iconKey(entity.getIconKey())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static GoalEntity toEntity(Goal domain) {
        if (domain == null) {
            return null;
        }

        GoalEntity entity = GoalEntity.builder()
                .userId(domain.getUserId())
                .title(domain.getTitle())
                .targetAmount(domain.getTargetAmount())
                .currentAmount(domain.getCurrentAmount())
                .deadline(domain.getDeadline())
                .status(domain.getStatus())
                .iconKey(domain.getIconKey())
                .build();

        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion() != null ? domain.getVersion() : 0L);

        return entity;
    }
}
