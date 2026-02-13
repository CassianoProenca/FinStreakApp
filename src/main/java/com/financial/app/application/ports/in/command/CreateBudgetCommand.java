package com.financial.app.application.ports.in.command;

import com.financial.app.domain.model.enums.TransactionCategory;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateBudgetCommand(
    UUID userId,
    TransactionCategory category,
    BigDecimal limitAmount,
    int month,
    int year
) {}
