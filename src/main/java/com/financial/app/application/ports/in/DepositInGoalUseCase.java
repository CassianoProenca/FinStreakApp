package com.financial.app.application.ports.in;

import com.financial.app.domain.model.GoalDeposit;
import java.math.BigDecimal;
import java.util.UUID;

public interface DepositInGoalUseCase {
    GoalDeposit execute(UUID userId, UUID goalId, BigDecimal amount, String description);
}
