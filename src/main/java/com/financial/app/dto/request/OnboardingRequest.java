package com.financial.app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record OnboardingRequest(
        @NotNull(message = "Initial balance is required")
        @PositiveOrZero(message = "Initial balance cannot be negative")
        BigDecimal initialBalance,

        String goalName // Opcional
) {}
