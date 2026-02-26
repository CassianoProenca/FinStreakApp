package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoadUpcomingTransactionsPort {
    List<Transaction> loadFutureInstallments(UUID userId, LocalDateTime after);
}
