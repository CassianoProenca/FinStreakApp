package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.in.DepositInGoalUseCase;
import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveAchievementPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Achievement;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.AchievementType;
import com.financial.app.domain.model.enums.GoalStatus;
import com.financial.app.domain.model.enums.NotificationType;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
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
    private final SaveTransactionPort saveTransactionPort;

    @Override
    public GoalDeposit execute(UUID userId, UUID goalId, BigDecimal amount, String description) {
        // 1. Carregar a meta e verificar se pertence ao usuário
        Goal goal = loadGoalsPort.loadByUserId(userId).stream()
                .filter(g -> g.getId().equals(goalId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        // 2. Block deposits on COMPLETED or CANCELLED goals (#15)
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new com.financial.app.domain.exception.BusinessException("Não é possível depositar em uma meta já concluída");
        }
        if (goal.getStatus() == GoalStatus.CANCELLED) {
            throw new com.financial.app.domain.exception.BusinessException("Não é possível depositar em uma meta cancelada");
        }

        // 3. Atualizar o valor atual da meta
        BigDecimal newAmount = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(newAmount);

        // 3. Verificar se a meta foi concluída
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        saveGoalPort.save(goal);

        // 4. Registrar no histórico da meta
        GoalDeposit deposit = GoalDeposit.builder()
                .goalId(goalId)
                .amount(amount)
                .description(description)
                .transactionDate(LocalDateTime.now())
                .build();

        GoalDeposit savedDeposit = goalHistoryPort.save(deposit);

        // 5. Criar transação de alocação (GOAL_ALLOCATION) para reduzir saldo disponível
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .amount(amount)
                .description("Aporte - " + goal.getTitle())
                .type(TransactionType.GOAL_ALLOCATION)
                .category(TransactionCategory.OTHER)
                .date(LocalDateTime.now())
                .iconKey(goal.getIconKey())
                .goalId(goalId)
                .build();
        saveTransactionPort.save(transaction);

        // 6. Ganhar Streak/XP por poupar
        checkStreakUseCase.execute(userId);

        // 7. Verificar achievements e notificação de meta concluída
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            // Fire GOAL_COMPLETED notification (#23)
            notificationPort.notifyUser(userId,
                    "🎯 Parabéns! Você concluiu a meta \"" + goal.getTitle() + "\"!",
                    NotificationType.GOAL_COMPLETED);
            checkAndAwardGoalAchievements(userId, goal.getTargetAmount());
        }

        return savedDeposit;
    }

    private void checkAndAwardGoalAchievements(UUID userId, BigDecimal targetAmount) {
        // GOAL_SETTER: primeira meta concluída — grant XP (#17)
        if (!loadAchievementsPort.hasAchievement(userId, AchievementType.GOAL_SETTER)) {
            awardAchievement(userId, AchievementType.GOAL_SETTER,
                    "Sonhador Realizado", "Você concluiu sua primeira meta financeira!", 300);
        }
    }

    private void awardAchievement(UUID userId, AchievementType type, String name, String description, long xp) {
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
