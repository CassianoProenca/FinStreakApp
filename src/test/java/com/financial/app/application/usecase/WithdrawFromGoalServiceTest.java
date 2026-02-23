package com.financial.app.application.usecase;

import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.exception.BusinessException;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.enums.GoalStatus;
import com.financial.app.domain.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawFromGoalServiceTest {

    @Mock private LoadGoalsPort loadGoalsPort;
    @Mock private SaveGoalPort saveGoalPort;
    @Mock private GoalHistoryPort goalHistoryPort;
    @Mock private SaveTransactionPort saveTransactionPort;

    @InjectMocks
    private WithdrawFromGoalService service;

    private UUID userId;
    private UUID goalId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        goalId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should withdraw successfully and create GOAL_WITHDRAWAL transaction")
    void shouldWithdrawSuccessfully() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .title("Reserva")
                .currentAmount(new BigDecimal("1000"))
                .targetAmount(new BigDecimal("5000"))
                .status(GoalStatus.IN_PROGRESS)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));
        when(goalHistoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        service.execute(userId, goalId, new BigDecimal("400"), "Emergência");

        assertEquals(new BigDecimal("600"), goal.getCurrentAmount());
        verify(saveGoalPort).save(goal);
        verify(goalHistoryPort).save(argThat(h -> h.getAmount().equals(new BigDecimal("-400"))));
        verify(saveTransactionPort).save(argThat(t -> t.getType() == TransactionType.GOAL_WITHDRAWAL && t.getAmount().equals(new BigDecimal("400"))));
    }

    @Test
    @DisplayName("Should throw exception if insufficient funds in goal")
    void shouldThrowIfInsufficientFunds() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("100"))
                .targetAmount(new BigDecimal("5000"))
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));

        assertThrows(BusinessException.class, () -> service.execute(userId, goalId, new BigDecimal("500"), "Resgate alto"));
        
        verify(saveGoalPort, never()).save(any());
    }

    @Test
    @DisplayName("Should revert status to IN_PROGRESS if withdrawal drops below target")
    void shouldRevertStatusToInProgress() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("5000"))
                .targetAmount(new BigDecimal("5000"))
                .status(GoalStatus.COMPLETED)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));
        when(goalHistoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        service.execute(userId, goalId, new BigDecimal("100"), "Resgate pequeno");

        assertEquals(new BigDecimal("4900"), goal.getCurrentAmount());
        assertEquals(GoalStatus.IN_PROGRESS, goal.getStatus());
        verify(saveGoalPort).save(goal);
    }
}
