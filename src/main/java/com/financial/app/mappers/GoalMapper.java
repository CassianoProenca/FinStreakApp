package com.financial.app.mappers;

import com.financial.app.dto.request.CreateGoalRequest;
import com.financial.app.dto.response.GoalResponse;
import com.financial.app.model.Goal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Component
public class GoalMapper {

    public Goal toEntity(CreateGoalRequest request, UUID userId) {
        return Goal.builder()
                .userId(userId)
                .title(request.title())
                .targetAmount(request.targetAmount())
                .currentAmount(request.currentAmount()) // Será tratado no Service se nulo
                .deadline(request.deadline())
                .icon(request.icon())
                .build();
    }

    public GoalResponse toResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getDeadline(),
                goal.getStatus(),
                goal.getIcon(),
                calculateProgress(goal.getCurrentAmount(), goal.getTargetAmount())
        );
    }

    private BigDecimal calculateProgress(BigDecimal current, BigDecimal target) {
        if (target == null || target.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.divide(target, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
