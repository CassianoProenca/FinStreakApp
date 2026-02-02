package com.financial.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OnboardingRequest(
        @Positive(message = "Monthly income must be positive")
        BigDecimal monthlyIncome,

        @Valid
        List<ExpenseRequest> fixedExpenses,

        @Valid
        GoalRequest mainGoal
) {
    public record ExpenseRequest(
            @NotBlank(message = "Expense name is required")
            String name,

            @NotNull(message = "Amount is required")
            @Positive(message = "Amount must be positive")
            BigDecimal amount,

            String category // Opcional, será convertido ou defaultado para OTHER
    ) {}

    public record GoalRequest(
            @NotBlank(message = "Goal title is required")
            String title,

            @NotNull(message = "Target amount is required")
            @Positive(message = "Target amount must be positive")
            BigDecimal targetAmount,

            @NotNull(message = "Deadline is required")
            @Future(message = "Deadline must be in the future")
            LocalDate deadline
    ) {}
}