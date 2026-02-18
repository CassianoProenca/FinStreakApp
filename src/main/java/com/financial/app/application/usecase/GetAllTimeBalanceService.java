package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.GetAllTimeBalanceUseCase;
import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.response.BalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllTimeBalanceService implements GetAllTimeBalanceUseCase {

    private final LoadTransactionPort loadTransactionPort;

    @Override
    public BalanceResponse execute(UUID userId) {
        // No date filter — loads all transactions for the user
        TransactionQuery query = new TransactionQuery(userId, null, null, null, null, 0, Integer.MAX_VALUE);
        List<Transaction> transactions = loadTransactionPort.loadAllByQuery(query);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BalanceResponse(totalIncome, totalExpenses, totalIncome.subtract(totalExpenses));
    }
}
