package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Transaction;
import java.util.List;

public interface LoadRecurringTransactionsPort {
    List<Transaction> loadActiveRecurring();
}
