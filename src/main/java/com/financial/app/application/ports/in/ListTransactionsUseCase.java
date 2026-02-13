package com.financial.app.application.ports.in;

import com.financial.app.domain.model.PagedResult;
import com.financial.app.domain.model.Transaction;
import java.util.UUID;

public interface ListTransactionsUseCase {
    PagedResult<Transaction> execute(TransactionQuery query);
}
