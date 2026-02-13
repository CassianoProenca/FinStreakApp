package com.financial.app.infrastructure.adapters.in.web.dto.response;

import java.math.BigDecimal;

public record BudgetSummary(
    String category,
    BigDecimal limitAmount,
    BigDecimal spentAmount,
    BigDecimal remainingAmount,
    double percentageUsed
) {}
