package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CreateGoalUseCase;
import com.financial.app.application.ports.in.command.CreateGoalCommand;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.enums.GoalStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateGoalService implements CreateGoalUseCase {

    private final SaveGoalPort saveGoalPort;

    public CreateGoalService(SaveGoalPort saveGoalPort) {
        this.saveGoalPort = saveGoalPort;
    }

    @Override
    public Goal execute(CreateGoalCommand command) {
        Goal goal = Goal.builder()
                .userId(command.userId())
                .title(command.title())
                .targetAmount(command.targetAmount())
                .currentAmount(command.currentAmount() != null ? command.currentAmount() : java.math.BigDecimal.ZERO)
                .deadline(command.deadline())
                .icon(command.icon())
                .status(GoalStatus.IN_PROGRESS)
                .build();
        
        goal.initialize(); // Set ID, createdAt

        return saveGoalPort.save(goal);
    }
}
