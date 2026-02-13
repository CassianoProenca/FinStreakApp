package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.DeleteGoalUseCase;
import com.financial.app.application.ports.out.DeleteGoalPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.domain.model.Goal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteGoalService implements DeleteGoalUseCase {

    private final LoadGoalsPort loadGoalsPort;
    private final DeleteGoalPort deleteGoalPort;

    @Override
    public void execute(UUID userId, UUID goalId) {
        Goal goal = loadGoalsPort.loadByUserId(userId).stream()
                .filter(g -> g.getId().equals(goalId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Goal not found or access denied"));

        deleteGoalPort.deleteById(goalId);
    }
}
