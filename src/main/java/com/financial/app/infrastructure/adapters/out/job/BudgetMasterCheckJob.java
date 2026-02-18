package com.financial.app.infrastructure.adapters.out.job;

import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.out.BudgetPort;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadAllUsersPort;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveAchievementPort;
import com.financial.app.domain.model.Achievement;
import com.financial.app.domain.model.Budget;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.AchievementType;
import com.financial.app.domain.model.enums.NotificationType;
import com.financial.app.domain.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BudgetMasterCheckJob {

    private final LoadAllUsersPort loadAllUsersPort;
    private final BudgetPort budgetPort;
    private final LoadTransactionPort loadTransactionPort;
    private final LoadAchievementsPort loadAchievementsPort;
    private final SaveAchievementPort saveAchievementPort;
    private final NotificationPort notificationPort;

    // Runs on the 1st of every month at 01:00
    @Scheduled(cron = "0 0 1 1 * *")
    public void checkBudgetMaster() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        int month = previousMonth.getMonthValue();
        int year = previousMonth.getYear();

        log.info("BudgetMasterCheckJob: checking {}/{}", month, year);

        List<UUID> userIds = loadAllUsersPort.loadAllUserIds();

        for (UUID userId : userIds) {
            try {
                processBudgetMasterForUser(userId, month, year);
            } catch (Exception e) {
                log.error("Error processing BUDGET_MASTER for user {}: {}", userId, e.getMessage());
            }
        }
    }

    private void processBudgetMasterForUser(UUID userId, int month, int year) {
        if (loadAchievementsPort.hasAchievement(userId, AchievementType.BUDGET_MASTER)) {
            return;
        }

        List<Budget> budgets = budgetPort.findByUserAndPeriod(userId, month, year);
        if (budgets.isEmpty()) {
            return;
        }

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        TransactionQuery query = new TransactionQuery(userId, startDate, endDate, TransactionType.EXPENSE, null, 0, Integer.MAX_VALUE);
        List<Transaction> expenses = loadTransactionPort.loadAllByQuery(query);

        Map<String, BigDecimal> spentByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().name(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        boolean allWithinBudget = budgets.stream().allMatch(budget -> {
            BigDecimal spent = spentByCategory.getOrDefault(budget.getCategory().name(), BigDecimal.ZERO);
            return spent.compareTo(budget.getLimitAmount()) <= 0;
        });

        if (allWithinBudget) {
            Achievement achievement = Achievement.builder()
                    .userId(userId)
                    .type(AchievementType.BUDGET_MASTER)
                    .name("Mestre do Orçamento")
                    .description("Você ficou dentro do orçamento em todas as categorias!")
                    .earnedAt(LocalDateTime.now())
                    .build();
            saveAchievementPort.save(achievement);
            notificationPort.notifyUser(userId, "🏆 Nova Medalha: Mestre do Orçamento!", NotificationType.ACHIEVEMENT);
            log.info("BUDGET_MASTER awarded to user {}", userId);
        }
    }
}
