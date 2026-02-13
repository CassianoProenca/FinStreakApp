package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.CreateBudgetCommand;
import com.financial.app.domain.model.Budget;
import java.util.List;
import java.util.UUID;

public interface BudgetUseCase {
    Budget createOrUpdate(CreateBudgetCommand command);
    List<Budget> listByUserAndPeriod(UUID userId, int month, int year);
}
