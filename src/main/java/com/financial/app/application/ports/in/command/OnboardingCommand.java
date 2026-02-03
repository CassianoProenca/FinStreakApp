package com.financial.app.application.ports.in.command;

import java.math.BigDecimal;
import java.util.UUID;

public record OnboardingCommand(
    UUID userId,
    BigDecimal monthlyIncome,
    BigDecimal monthlySavingsGoal
) {}
