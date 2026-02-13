package com.financial.app.infrastructure.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record GoalDepositRequest(
    @NotNull @Positive BigDecimal amount,
    String description
) {}
