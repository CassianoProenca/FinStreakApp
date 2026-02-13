package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.Transaction;
import com.financial.app.infrastructure.adapters.out.persistence.entity.TransactionEntity;

public class TransactionMapper {

    public static Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }

        return Transaction.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .description(entity.getDescription())
                .type(entity.getType())
                .category(entity.getCategory())
                .date(entity.getDate())
                .isRecurring(entity.isRecurring())
                .frequency(entity.getFrequency())
                .repeatDay(entity.getRepeatDay())
                .iconKey(entity.getIconKey())
                .parentTransactionId(entity.getParentTransactionId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static TransactionEntity toEntity(Transaction domain) {
        if (domain == null) {
            return null;
        }

        TransactionEntity entity = TransactionEntity.builder()
                .userId(domain.getUserId())
                .amount(domain.getAmount())
                .description(domain.getDescription())
                .type(domain.getType())
                .category(domain.getCategory())
                .date(domain.getDate())
                .isRecurring(domain.isRecurring())
                .frequency(domain.getFrequency())
                .repeatDay(domain.getRepeatDay())
                .iconKey(domain.getIconKey())
                .parentTransactionId(domain.getParentTransactionId())
                .build();

        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion() != null ? domain.getVersion() : 0L);

        return entity;
    }
}
