package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.in.DepositInGoalUseCase;
import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveAchievementPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.domain.model.Achievement;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.domain.model.enums.AchievementType;
import com.financial.app.domain.model.enums.GoalStatus;
import com.financial.app.domain.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositInGoalService implements DepositInGoalUseCase {

    private final LoadGoalsPort loadGoalsPort;
    private final SaveGoalPort saveGoalPort;
    private final GoalHistoryPort goalHistoryPort;
    private final CheckStreakUseCase checkStreakUseCase;
    private final LoadAchievementsPort loadAchievementsPort;
    private final SaveAchievementPort saveAchievementPort;
    private final NotificationPort notificationPort;

    @Override
    public GoalDeposit execute(UUID userId, UUID goalId, BigDecimal amount, String description) {
        // 1. Carregar a meta e verificar se pertence ao usuário
        Goal goal = loadGoalsPort.loadByUserId(userId).stream()
                .filter(g -> g.getId().equals(goalId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Goal not found or access denied"));

        // 2. Atualizar o valor atual da meta
        BigDecimal newAmount = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(newAmount);

        // 3. Verificar se a meta foi concluída
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        saveGoalPort.save(goal);

        // 4. Registrar no histórico
        GoalDeposit deposit = GoalDeposit.builder()
                .goalId(goalId)
                .amount(amount)
                .description(description)
                .transactionDate(LocalDateTime.now())
                .build();

        GoalDeposit savedDeposit = goalHistoryPort.save(deposit);

        // 5. Ganhar Streak/XP por poupar
        checkStreakUseCase.execute(userId);

        // 6. Verificar achievements de meta concluída
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            checkAndAwardGoalAchievements(userId, goal.getTargetAmount());
        }

        return savedDeposit;
    }

    private void checkAndAwardGoalAchievements(UUID userId, BigDecimal targetAmount) {
        // GOAL_SETTER: primeira meta concluída (qualquer valor)
        if (!loadAchievementsPort.hasAchievement(userId, AchievementType.GOAL_SETTER)) {
            awardAchievement(userId, AchievementType.GOAL_SETTER,
                    "Sonhador Realizado", "Você concluiu sua primeira meta financeira!");
        }

        // ELITE_SAVER: meta concluída com targetAmount >= 1000
        if (targetAmount.compareTo(new BigDecimal("1000")) >= 0
                && !loadAchievementsPort.hasAchievement(userId, AchievementType.ELITE_SAVER)) {
            awardAchievement(userId, AchievementType.ELITE_SAVER,
                    "Poupador de Elite", "Você concluiu uma meta de R$1.000 ou mais!");
        }
    }

    private void awardAchievement(UUID userId, AchievementType type, String name, String description) {
        Achievement achievement = Achievement.builder()
                .userId(userId)
                .type(type)
                .name(name)
                .description(description)
                .earnedAt(LocalDateTime.now())
                .build();
        saveAchievementPort.save(achievement);
        notificationPort.notifyUser(userId, "🏆 Nova Medalha: " + name + "!", NotificationType.ACHIEVEMENT);
    }
}
