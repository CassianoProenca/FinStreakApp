package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.domain.model.enums.GoalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalDepositUseCaseTest {

    @Mock private LoadGoalsPort loadGoalsPort;
    @Mock private SaveGoalPort saveGoalPort;
    @Mock private GoalHistoryPort goalHistoryPort;
    @Mock private CheckStreakUseCase checkStreakUseCase;

    @InjectMocks
    private DepositInGoalService service;

    @Test
    @DisplayName("Should update goal balance and create history record")
    void shouldDepositSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("1000"))
                .targetAmount(new BigDecimal("5000"))
                .status(GoalStatus.IN_PROGRESS)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));
        when(goalHistoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        service.execute(userId, goalId, new BigDecimal("500"), "Poupança mensal");

        assertEquals(new BigDecimal("1500"), goal.getCurrentAmount());
        verify(saveGoalPort, times(1)).save(goal);
        verify(goalHistoryPort, times(1)).save(any(GoalDeposit.class));
        verify(checkStreakUseCase, times(1)).execute(userId);
    }
}
