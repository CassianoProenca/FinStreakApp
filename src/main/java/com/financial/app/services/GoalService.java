package com.financial.app.services;

import com.financial.app.dto.request.CreateGoalRequest;
import com.financial.app.dto.response.GoalResponse;
import com.financial.app.mappers.GoalMapper;
import com.financial.app.model.Goal;
import com.financial.app.model.enums.GoalStatus;
import com.financial.app.repositories.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    @Transactional
    public GoalResponse create(UUID userId, CreateGoalRequest request) {
        if (request.deadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The deadline cannot be in the past.");
        }

        Goal goal = goalMapper.toEntity(request, userId);

        // Regra 1: Inicializar currentAmount como 0 se nulo
        if (goal.getCurrentAmount() == null) {
            goal.setCurrentAmount(BigDecimal.ZERO);
        }

        // Regra 2: Status inicial IN_PROGRESS
        goal.setStatus(GoalStatus.IN_PROGRESS);

        Goal savedGoal = goalRepository.save(goal);
        return goalMapper.toResponse(savedGoal);
    }

    @Transactional
    public GoalResponse addAmount(UUID goalId, BigDecimal amount, UUID userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!goal.getUserId().equals(userId)) {
            throw new SecurityException("Access denied");
        }

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            return goalMapper.toResponse(goal);
        }

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));

        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        return goalMapper.toResponse(goalRepository.save(goal));
    }

    public List<GoalResponse> listGoals(UUID userId) {
        return goalRepository.findByUserId(userId)
                .stream()
                .map(goalMapper::toResponse)
                .toList();
    }
}
