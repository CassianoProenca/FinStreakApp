package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.DeleteBudgetUseCase;
import com.financial.app.application.ports.out.BudgetPort;
import com.financial.app.application.ports.out.DeleteBudgetPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.model.Budget;
import com.financial.app.domain.model.enums.TransactionCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteBudgetService implements DeleteBudgetUseCase {

    private final BudgetPort budgetPort;
    private final DeleteBudgetPort deleteBudgetPort;

    @Override
    public void execute(UUID userId, TransactionCategory category, int month, int year) {
        Budget budget = budgetPort.findByUserCategoryAndPeriod(userId, category, month, year)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado"));

        deleteBudgetPort.deleteById(budget.getId());
    }
}
