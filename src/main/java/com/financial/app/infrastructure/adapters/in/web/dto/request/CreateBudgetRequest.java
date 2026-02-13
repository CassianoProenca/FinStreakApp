package com.financial.app.infrastructure.adapters.in.web.dto.request;

import com.financial.app.domain.model.enums.TransactionCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateBudgetRequest(
    @NotNull TransactionCategory category,
    @NotNull @Positive BigDecimal limitAmount,
    Integer month,
    Integer year
) {}
