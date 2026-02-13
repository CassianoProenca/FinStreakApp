package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.GetDashboardSummaryUseCase;
import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.out.BudgetPort;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.domain.model.Achievement;
import com.financial.app.domain.model.Budget;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.response.BudgetSummary;
import com.financial.app.infrastructure.adapters.in.web.dto.response.DashboardSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetDashboardSummaryService implements GetDashboardSummaryUseCase {

    private final LoadTransactionPort loadTransactionPort;
    private final LoadGamificationProfilePort loadGamificationProfilePort;
    private final BudgetPort budgetPort;
    private final LoadAchievementsPort loadAchievementsPort;

    public GetDashboardSummaryService(LoadTransactionPort loadTransactionPort,
                                      LoadGamificationProfilePort loadGamificationProfilePort,
                                      BudgetPort budgetPort,
                                      LoadAchievementsPort loadAchievementsPort) {
        this.loadTransactionPort = loadTransactionPort;
        this.loadGamificationProfilePort = loadGamificationProfilePort;
        this.budgetPort = budgetPort;
        this.loadAchievementsPort = loadAchievementsPort;
    }

    @Override
    public DashboardSummaryResponse execute(UUID userId, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        TransactionQuery query = new TransactionQuery(userId, start, end, null, null, 0, Integer.MAX_VALUE);
        List<Transaction> transactions = loadTransactionPort.loadAllByQuery(query);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> spendingByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().name(),
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        // Calculate Budgets
        List<Budget> budgetList = budgetPort.findByUserAndPeriod(userId, month, year);
        List<BudgetSummary> budgets = budgetList.stream().map(b -> {
            BigDecimal spent = spendingByCategory.getOrDefault(b.getCategory().name(), BigDecimal.ZERO);
            BigDecimal remaining = b.getLimitAmount().subtract(spent);
            double percentage = 0.0;
            if (b.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
                percentage = spent.divide(b.getLimitAmount(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
            }
            return new BudgetSummary(b.getCategory().name(), b.getLimitAmount(), spent, remaining, percentage);
        }).collect(Collectors.toList());

        // Get Achievements
        List<Achievement> achievements = loadAchievementsPort.loadByUserId(userId);

        int currentStreak = loadGamificationProfilePort.loadByUserId(userId)
                .map(GamificationProfile::getCurrentStreak)
                .orElse(0);

        return new DashboardSummaryResponse(totalIncome, totalExpenses, balance, spendingByCategory, budgets, achievements, currentStreak);
    }
}
