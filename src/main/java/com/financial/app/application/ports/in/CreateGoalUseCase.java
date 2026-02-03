package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.CreateGoalCommand;
import com.financial.app.domain.model.Goal;

public interface CreateGoalUseCase {
    Goal execute(CreateGoalCommand command);
}
