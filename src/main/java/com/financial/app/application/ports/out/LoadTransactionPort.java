package com.financial.app.application.ports.out;

import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.domain.model.PagedResult;
import com.financial.app.domain.model.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadTransactionPort {
    Optional<Transaction> loadById(UUID id);
    PagedResult<Transaction> loadByQuery(TransactionQuery query);
    List<Transaction> loadAllByQuery(TransactionQuery query);
}
