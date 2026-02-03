package com.financial.app.application.ports.in;

import com.financial.app.domain.model.Transaction;
import java.util.List;
import java.util.UUID;

public interface ListTransactionsUseCase {
    List<Transaction> execute(UUID userId);
}
