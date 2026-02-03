package com.financial.app.application.ports.in.command;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTransactionCommand(
    UUID userId,
    BigDecimal amount,
    String description,
    TransactionType type,
    TransactionCategory category,
    LocalDateTime date
) {}

