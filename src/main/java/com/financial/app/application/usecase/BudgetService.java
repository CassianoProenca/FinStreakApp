package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.BudgetUseCase;
import com.financial.app.application.ports.in.command.CreateBudgetCommand;
import com.financial.app.application.ports.out.BudgetPort;
import com.financial.app.domain.model.Budget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService implements BudgetUseCase {

    private final BudgetPort budgetPort;

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
}
