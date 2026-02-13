package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CompleteOnboardingUseCase;
import com.financial.app.application.ports.in.command.OnboardingCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.application.ports.out.SaveUserPort;
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isOnboardingCompleted()) {
            throw new IllegalStateException("Onboarding already completed");
        }

        // 1. Set Monthly Income on User (Point 2)
        if (command.monthlyIncome() != null) {
            user.setMonthlyIncome(command.monthlyIncome());
        }

        // 2. Create Income Transaction (Salary) - Still useful to have a history record
        if (command.monthlyIncome() != null && command.monthlyIncome().compareTo(BigDecimal.ZERO) > 0) {
            Transaction income = Transaction.builder()
                    .userId(user.getId())
                    .amount(command.monthlyIncome())
                    .description("Monthly Income (Initial)")
                    .type(TransactionType.INCOME)
                    .category(TransactionCategory.SALARY)
                    .date(LocalDateTime.now())
                    .iconKey("salary") // Default icon for salary
                    .build();
            saveTransactionPort.save(income);
        }

        // 3. Create Fixed Expenses (Point 3 - Recurrence)
        if (command.fixedExpenses() != null) {
            command.fixedExpenses().forEach(expense -> {
                Transaction tx = Transaction.builder()
                        .userId(user.getId())
                        .amount(expense.amount())
                        .description(expense.name())
                        .type(TransactionType.EXPENSE)
                        .category(expense.category() != null ? TransactionCategory.valueOf(expense.category().toUpperCase()) : TransactionCategory.OTHER)
                        .date(LocalDateTime.now())
                        .isRecurring(true)
                        .frequency("MONTHLY")
                        .iconKey(expense.iconKey())
                        .build();
                saveTransactionPort.save(tx);
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
