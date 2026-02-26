package com.financial.app.application.ports.in.command;

import java.math.BigDecimal;

/**
 * Domain-level value object for a fixed expense provided during onboarding.
 * Replaces the direct use of OnboardingRequest.ExpenseRequest in the command (#29).
 */
public record OnboardingExpenseItem(
        String name,
        BigDecimal amount,
        String category,
        String iconKey
) {}

