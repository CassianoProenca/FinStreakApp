package com.financial.app.application.ports.in.command;

import com.financial.app.infrastructure.adapters.in.web.dto.request.OnboardingRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OnboardingCommand(
    UUID userId,
    BigDecimal monthlyIncome,
    List<OnboardingRequest.ExpenseRequest> fixedExpenses,
    OnboardingRequest.GoalRequest mainGoal
) {}
