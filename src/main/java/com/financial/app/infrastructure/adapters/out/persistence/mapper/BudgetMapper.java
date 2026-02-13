package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.Budget;
import com.financial.app.infrastructure.adapters.out.persistence.entity.BudgetEntity;

public class BudgetMapper {
    public static Budget toDomain(BudgetEntity entity) {
        if (entity == null) return null;
        return Budget.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .category(entity.getCategory())
                .limitAmount(entity.getLimitAmount())
                .month(entity.getMonth())
                .year(entity.getYear())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static BudgetEntity toEntity(Budget domain) {
        if (domain == null) return null;
        BudgetEntity entity = BudgetEntity.builder()
                .userId(domain.getUserId())
                .category(domain.getCategory())
                .limitAmount(domain.getLimitAmount())
                .month(domain.getMonth())
                .year(domain.getYear())
                .build();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }
}
