package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Transaction;
import java.util.List;
import java.util.UUID;

public interface LoadTransactionPort {
    List<Transaction> loadByUserId(UUID userId);
}
