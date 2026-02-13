package com.financial.app.application.ports.in;

import com.financial.app.domain.model.enums.TransactionCategory;
import java.util.UUID;

public interface DeleteBudgetUseCase {
    void execute(UUID userId, TransactionCategory category, int month, int year);
}
