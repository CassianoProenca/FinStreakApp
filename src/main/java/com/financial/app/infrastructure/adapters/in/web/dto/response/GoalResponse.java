package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.GoalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GoalResponse(
        UUID id,
        String title,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        Double progressPercentage,
        LocalDateTime deadline,
        GoalStatus status,
        String iconKey
) {}