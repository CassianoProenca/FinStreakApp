package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.UpdateGoalUseCase;
import com.financial.app.application.ports.in.command.CreateGoalCommand;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.enums.GoalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateGoalService implements UpdateGoalUseCase {

    private final LoadGoalsPort loadGoalsPort;
    private final SaveGoalPort saveGoalPort;

    @Override
    public Goal execute(UUID userId, UUID goalId, CreateGoalCommand command) {
        Goal goal = loadGoalsPort.loadByUserId(userId).stream()
                .filter(g -> g.getId().equals(goalId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        goal.setTitle(command.title());
        goal.setTargetAmount(command.targetAmount());
        goal.setDeadline(command.deadline());
        goal.setIconKey(command.iconKey());

        // Recalcular status se necessário
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        } else {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }

        return saveGoalPort.save(goal);
    }
}
