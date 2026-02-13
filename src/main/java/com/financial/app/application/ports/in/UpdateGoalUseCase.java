package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.CreateGoalCommand;
import com.financial.app.domain.model.Goal;
import java.util.UUID;

public interface UpdateGoalUseCase {
    Goal execute(UUID userId, UUID goalId, CreateGoalCommand command);
}
