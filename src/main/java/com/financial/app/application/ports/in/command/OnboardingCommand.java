package com.financial.app.application.ports.in.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Pure domain command — no dependency on infrastructure DTOs (#29).
 */
public record OnboardingCommand(
    UUID userId,
    BigDecimal monthlyIncome,
    List<OnboardingExpenseItem> fixedExpenses,
    OnboardingGoalItem mainGoal
) {}
