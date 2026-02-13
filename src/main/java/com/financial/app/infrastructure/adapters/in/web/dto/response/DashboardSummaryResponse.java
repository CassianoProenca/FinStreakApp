package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.Achievement;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardSummaryResponse(
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal balance,
    Map<String, BigDecimal> spendingByCategory,
    List<BudgetSummary> budgets,
    List<Achievement> achievements,
    int currentStreak
) {}
