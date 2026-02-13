package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Budget;
import com.financial.app.domain.model.enums.TransactionCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetPort {
    Budget save(Budget budget);
    Optional<Budget> findByUserCategoryAndPeriod(UUID userId, TransactionCategory category, int month, int year);
    List<Budget> findByUserAndPeriod(UUID userId, int month, int year);
}
