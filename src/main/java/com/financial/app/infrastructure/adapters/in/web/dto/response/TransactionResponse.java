package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        BigDecimal amount,
        String description,
        TransactionType type,
        TransactionCategory category,
        LocalDateTime date,
        LocalDateTime createdAt,
        boolean isRecurring,
        String frequency,
        Integer repeatDay,
        String iconKey
) {}