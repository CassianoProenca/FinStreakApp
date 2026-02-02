package com.financial.app.mappers;

import com.financial.app.dto.request.CreateTransactionRequest;
import com.financial.app.dto.response.TransactionResponse;
import com.financial.app.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransactionMapper {

    public Transaction toEntity(CreateTransactionRequest request, UUID userId) {
        return Transaction.builder()
                .userId(userId)
                .amount(request.amount())
                .description(request.description())
                .type(request.type())
                .category(request.category())
                // Se a data vier nula, usa o momento atual (Input Rápido)
                .date(request.date() != null ? request.date() : LocalDateTime.now())
                .build();
    }

    public TransactionResponse toResponse(Transaction entity) {
        return new TransactionResponse(
                entity.getId(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getType(),
                entity.getCategory(),
                entity.getDate(),
                entity.getCreatedAt()
        );
    }
}