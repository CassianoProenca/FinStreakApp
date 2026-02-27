package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CompleteOnboardingUseCase;
import com.financial.app.application.ports.in.command.OnboardingCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.exception.BusinessException;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.User;
import com.financial.app.domain.model.enums.GoalStatus;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class CompleteOnboardingService implements CompleteOnboardingUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final SaveTransactionPort saveTransactionPort;
    private final SaveGoalPort saveGoalPort;

    public CompleteOnboardingService(LoadUserPort loadUserPort, 
                                     SaveUserPort saveUserPort,
                                     SaveTransactionPort saveTransactionPort,
                                     SaveGoalPort saveGoalPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.saveTransactionPort = saveTransactionPort;
        this.saveGoalPort = saveGoalPort;
    }

    @Override
    public void execute(OnboardingCommand command) {
        User user = loadUserPort.loadById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (user.isOnboardingCompleted()) {
            throw new BusinessException("Onboarding já realizado");
        }

        // 1. Set Monthly Income on User
        if (command.monthlyIncome() != null) {
            user.setMonthlyIncome(command.monthlyIncome());

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime firstOfMonth = now.withDayOfMonth(1).withHour(9).withMinute(0);

            // Create the Recurring Template
            Transaction incomeTemplate = Transaction.builder()
                    .userId(user.getId())
                    .amount(command.monthlyIncome())
                    .description("Renda Mensal (Recorrência)")
                    .type(TransactionType.INCOME)
                    .category(TransactionCategory.SALARY)
                    .date(now)
                    .isRecurring(true)
                    .frequency("MONTHLY")
                    .repeatDay(command.monthlyIncomeDay() != null ? command.monthlyIncomeDay() : 1)
                    .endRecurrenceDate(now.plusMonths(12))
                    .iconKey("banknote")
                    .build();
            saveTransactionPort.save(incomeTemplate);

            // Create the first REAL transaction for this month
            Transaction initialIncome = Transaction.builder()
                    .userId(user.getId())
                    .amount(command.monthlyIncome())
                    .description("Renda Mensal")
                    .type(TransactionType.INCOME)
                    .category(TransactionCategory.SALARY)
                    .date(firstOfMonth)
                    .isRecurring(false)
                    .iconKey("banknote")
                    .build();
            saveTransactionPort.save(initialIncome);
        }

        // 2. Create Fixed Expenses
        if (command.fixedExpenses() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime firstOfMonth = now.withDayOfMonth(1).withHour(10).withMinute(0);

            command.fixedExpenses().forEach(expense -> {
                // Template
                Transaction expenseTemplate = Transaction.builder()
                        .userId(user.getId())
                        .amount(expense.amount())
                        .description(expense.name() + " (Recorrência)")
                        .type(TransactionType.EXPENSE)
                        .category(expense.category() != null ? TransactionCategory.valueOf(expense.category().toUpperCase()) : TransactionCategory.OTHER)
                        .date(now)
                        .isRecurring(true)
                        .frequency("MONTHLY")
                        .endRecurrenceDate(now.plusMonths(3))
                        .iconKey(expense.iconKey())
                        .build();
                saveTransactionPort.save(expenseTemplate);

                // Real transaction for current month
                Transaction initialExpense = Transaction.builder()
                        .userId(user.getId())
                        .amount(expense.amount())
                        .description(expense.name())
                        .type(TransactionType.EXPENSE)
                        .category(expense.category() != null ? TransactionCategory.valueOf(expense.category().toUpperCase()) : TransactionCategory.OTHER)
                        .date(firstOfMonth)
                        .isRecurring(false)
                        .iconKey(expense.iconKey())
                        .build();
                saveTransactionPort.save(initialExpense);
            });
        }

        // 4. Create Main Goal (Point 5 & 6)
        if (command.mainGoal() != null) {
            Goal goal = Goal.builder()
                    .userId(user.getId())
                    .title(command.mainGoal().title())
                    .targetAmount(command.mainGoal().targetAmount())
                    .currentAmount(BigDecimal.ZERO)
                    .deadline(command.mainGoal().deadline())
                    .status(GoalStatus.IN_PROGRESS)
                    .iconKey(command.mainGoal().iconKey())
                    .build();
            saveGoalPort.save(goal);
        }

        // 5. Complete User Onboarding (Point 1)
        user.completeOnboarding();
        saveUserPort.save(user);
    }
}
