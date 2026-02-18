package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveAchievementPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.domain.model.enums.AchievementType;
import com.financial.app.domain.model.enums.GoalStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalDepositUseCaseTest {

    @Mock private LoadGoalsPort loadGoalsPort;
    @Mock private SaveGoalPort saveGoalPort;
    @Mock private GoalHistoryPort goalHistoryPort;
    @Mock private CheckStreakUseCase checkStreakUseCase;
    @Mock private LoadAchievementsPort loadAchievementsPort;
    @Mock private SaveAchievementPort saveAchievementPort;
    @Mock private NotificationPort notificationPort;

    @InjectMocks
    private DepositInGoalService service;

    private UUID userId;
    private UUID goalId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        goalId = UUID.randomUUID();
        when(goalHistoryPort.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("Should update goal balance and create history record")
    void shouldDepositSuccessfully() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("1000"))
                .targetAmount(new BigDecimal("5000"))
                .status(GoalStatus.IN_PROGRESS)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));

        service.execute(userId, goalId, new BigDecimal("500"), "Poupança mensal");

        assertEquals(new BigDecimal("1500"), goal.getCurrentAmount());
        assertEquals(GoalStatus.IN_PROGRESS, goal.getStatus()); // not yet completed
        verify(saveGoalPort).save(goal);
        verify(goalHistoryPort).save(any(GoalDeposit.class));
        verify(checkStreakUseCase).execute(userId);
        verify(saveAchievementPort, never()).save(any()); // no achievement yet
    }

    @Test
    @DisplayName("Should award GOAL_SETTER when first goal is completed")
    void shouldAwardGoalSetterOnCompletion() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("900"))
                .targetAmount(new BigDecimal("1000"))
                .status(GoalStatus.IN_PROGRESS)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.GOAL_SETTER)).thenReturn(false);
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.ELITE_SAVER)).thenReturn(true);

        service.execute(userId, goalId, new BigDecimal("100"), "Depósito final");

        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
        verify(saveAchievementPort).save(argThat(a -> a.getType() == AchievementType.GOAL_SETTER));
        verify(saveAchievementPort, never()).save(argThat(a -> a.getType() == AchievementType.ELITE_SAVER));
    }

    @Test
    @DisplayName("Should award ELITE_SAVER when goal with target >= 1000 is completed")
    void shouldAwardEliteSaverForHighValueGoal() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("4500"))
                .targetAmount(new BigDecimal("5000"))
                .status(GoalStatus.IN_PROGRESS)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.GOAL_SETTER)).thenReturn(false);
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.ELITE_SAVER)).thenReturn(false);

        service.execute(userId, goalId, new BigDecimal("500"), "Meta concluída");

        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
        verify(saveAchievementPort).save(argThat(a -> a.getType() == AchievementType.GOAL_SETTER));
        verify(saveAchievementPort).save(argThat(a -> a.getType() == AchievementType.ELITE_SAVER));
    }

    @Test
    @DisplayName("Should NOT award ELITE_SAVER when goal target is below 1000")
    void shouldNotAwardEliteSaverForLowValueGoal() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("400"))
                .targetAmount(new BigDecimal("500"))
                .status(GoalStatus.IN_PROGRESS)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.GOAL_SETTER)).thenReturn(false);

        service.execute(userId, goalId, new BigDecimal("100"), "Meta pequena");

        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
        verify(saveAchievementPort).save(argThat(a -> a.getType() == AchievementType.GOAL_SETTER));
        verify(saveAchievementPort, never()).save(argThat(a -> a.getType() == AchievementType.ELITE_SAVER));
    }

    @Test
    @DisplayName("Should NOT award achievements when goal is not yet completed")
    void shouldNotAwardAchievementsWhenGoalNotComplete() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("100"))
                .targetAmount(new BigDecimal("5000"))
                .status(GoalStatus.IN_PROGRESS)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));

        service.execute(userId, goalId, new BigDecimal("200"), "Depósito parcial");

        assertEquals(GoalStatus.IN_PROGRESS, goal.getStatus());
        verify(saveAchievementPort, never()).save(any());
        verify(loadAchievementsPort, never()).hasAchievement(any(), any());
    }

    @Test
    @DisplayName("Should NOT award GOAL_SETTER if already earned")
    void shouldNotAwardGoalSetterIfAlreadyEarned() {
        Goal goal = Goal.builder()
                .id(goalId)
                .userId(userId)
                .currentAmount(new BigDecimal("900"))
                .targetAmount(new BigDecimal("1000"))
                .status(GoalStatus.IN_PROGRESS)
                .build();

        when(loadGoalsPort.loadByUserId(userId)).thenReturn(List.of(goal));
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.GOAL_SETTER)).thenReturn(true);
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.ELITE_SAVER)).thenReturn(true);

        service.execute(userId, goalId, new BigDecimal("100"), "Meta concluída");

        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
        verify(saveAchievementPort, never()).save(any());
    }
}
