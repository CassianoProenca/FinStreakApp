package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Transaction;

public interface SaveTransactionPort {
    Transaction save(Transaction transaction);
}
