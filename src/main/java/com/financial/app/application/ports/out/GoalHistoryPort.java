package com.financial.app.application.ports.out;

import com.financial.app.domain.model.GoalDeposit;
import java.util.List;
import java.util.UUID;

public interface GoalHistoryPort {
    GoalDeposit save(GoalDeposit deposit);
    List<GoalDeposit> findByGoalId(UUID goalId);
}
