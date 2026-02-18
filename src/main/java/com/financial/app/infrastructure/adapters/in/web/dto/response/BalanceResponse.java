package com.financial.app.infrastructure.adapters.in.web.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance
) {}
