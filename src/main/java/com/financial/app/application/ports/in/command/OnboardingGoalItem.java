package com.financial.app.application.ports.in.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain-level value object for the main goal provided during onboarding.
 * Replaces the direct use of OnboardingRequest.GoalRequest in the command (#29).
 */
public record OnboardingGoalItem(
        String title,
        BigDecimal targetAmount,
        LocalDateTime deadline,
        String iconKey
) {}

