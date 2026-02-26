package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.DeleteGoalUseCase;
import com.financial.app.application.ports.out.DeleteGoalPort;
import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.exception.UnauthorizedAccessException;
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
    private final GoalHistoryPort goalHistoryPort;

    @Override
    public void execute(UUID userId, UUID goalId) {
        Goal goal = loadGoalsPort.loadByUserId(userId).stream()
                .filter(g -> g.getId().equals(goalId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!goal.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para excluir esta meta");
        }

        // Delete history first to avoid FK violation
        goalHistoryPort.deleteByGoalId(goalId);
        deleteGoalPort.deleteById(goalId);
    }
}
