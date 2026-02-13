package com.financial.app.application.ports.in;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionQuery(
    UUID userId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    TransactionType type,
    TransactionCategory category,
    int page,
    int size
) {
    public TransactionQuery {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
    }
}
