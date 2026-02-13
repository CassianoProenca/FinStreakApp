package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.infrastructure.adapters.out.persistence.entity.GoalHistoryEntity;

public class GoalHistoryMapper {
    public static GoalDeposit toDomain(GoalHistoryEntity entity) {
        if (entity == null) return null;
        return GoalDeposit.builder()
                .id(entity.getId())
                .goalId(entity.getGoalId())
                .amount(entity.getAmount())
                .description(entity.getDescription())
                .transactionDate(entity.getTransactionDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static GoalHistoryEntity toEntity(GoalDeposit domain) {
        if (domain == null) return null;
        GoalHistoryEntity entity = GoalHistoryEntity.builder()
                .goalId(domain.getGoalId())
                .amount(domain.getAmount())
                .description(domain.getDescription())
                .transactionDate(domain.getTransactionDate())
                .build();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }
}
