package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.ListTransactionsUseCase;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.domain.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListTransactionsService implements ListTransactionsUseCase {

    private final LoadTransactionPort loadTransactionPort;

    public ListTransactionsService(LoadTransactionPort loadTransactionPort) {
        this.loadTransactionPort = loadTransactionPort;
    }

    @Override
    public List<Transaction> execute(UUID userId) {
        return loadTransactionPort.loadByUserId(userId);
    }
}
