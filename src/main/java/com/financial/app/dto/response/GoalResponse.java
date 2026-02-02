package com.financial.app.dto.response;

import com.financial.app.model.enums.GoalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GoalResponse(
        UUID id,
        String title,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate deadline,
        GoalStatus status,
        String icon,
        BigDecimal progressPercentage // Campo calculado útil para o front
) {}
