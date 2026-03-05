package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.BudgetUseCase;
import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.in.command.CreateBudgetCommand;
import com.financial.app.application.ports.out.BudgetPort;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.domain.model.Budget;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.NotificationType;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService implements BudgetUseCase {

    private final BudgetPort budgetPort;
    private final LoadTransactionPort loadTransactionPort;
    private final NotificationPort notificationPort;

    @Override
    public Budget createOrUpdate(CreateBudgetCommand command) {
        Optional<Budget> existingBudget = budgetPort.findByUserCategoryAndPeriod(
                command.userId(), command.category(), command.month(), command.year()
        );

        Budget budget;
        if (existingBudget.isPresent()) {
            budget = existingBudget.get();
            budget.setLimitAmount(command.limitAmount());
        } else {
            budget = Budget.builder()
                    .userId(command.userId())
                    .category(command.category())
                    .limitAmount(command.limitAmount())
                    .month(command.month())
                    .year(command.year())
                    .build();
        }

        return budgetPort.save(budget);
    }

    @Override
    public List<Budget> listByUserAndPeriod(UUID userId, int month, int year) {
        return budgetPort.findByUserAndPeriod(userId, month, year);
    }

    /**
     * Verifica se o usuário ultrapassou 80% ou 100% do limite de orçamento
     * para a categoria da transação recém-criada e envia notificação quando necessário.
     * Chamado por CreateTransactionService após salvar uma despesa (EXPENSE).
     */
    public void checkAndAlertBudget(UUID userId, TransactionCategory category, int month, int year) {
        budgetPort.findByUserCategoryAndPeriod(userId, category, month, year)
                .ifPresent(budget -> {
                    if (budget.getLimitAmount() == null || budget.getLimitAmount().compareTo(BigDecimal.ZERO) == 0) {
                        return;
                    }

                    YearMonth ym = YearMonth.of(budget.getYear(), budget.getMonth());
                    LocalDateTime start = ym.atDay(1).atStartOfDay();
                    LocalDateTime end = ym.atEndOfMonth().atTime(LocalTime.MAX);

                    TransactionQuery query = new TransactionQuery(
                            userId, start, end, TransactionType.EXPENSE, category, 0, Integer.MAX_VALUE
                    );
                    List<Transaction> expenses = loadTransactionPort.loadAllByQuery(query);

                    BigDecimal spent = expenses.stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal percentage = spent
                            .divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

                    String categoryName = category.name();

                    if (percentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
                        notificationPort.notifyUser(
                                userId,
                                "Você atingiu 100% do orçamento de " + categoryName + "! Limite: R$ " + budget.getLimitAmount(),
                                NotificationType.BUDGET_ALERT
                        );
                    } else if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
                        notificationPort.notifyUser(
                                userId,
                                "Você usou " + percentage.setScale(0, RoundingMode.HALF_UP) + "% do orçamento de " + categoryName + ". Limite: R$ " + budget.getLimitAmount(),
                                NotificationType.BUDGET_ALERT
                        );
                    }
                });
    }
}
