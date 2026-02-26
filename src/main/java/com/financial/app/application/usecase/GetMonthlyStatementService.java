package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.GetMonthlyStatementUseCase;
import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.response.MonthlyStatementResponse;
import com.financial.app.infrastructure.adapters.in.web.dto.response.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetMonthlyStatementService implements GetMonthlyStatementUseCase {

    private final LoadTransactionPort loadTransactionPort;

    public GetMonthlyStatementService(LoadTransactionPort loadTransactionPort) {
        this.loadTransactionPort = loadTransactionPort;
    }

    @Override
    public MonthlyStatementResponse execute(UUID userId, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        // Opening balance: all transactions strictly before the month
        TransactionQuery priorQuery = new TransactionQuery(userId, null, monthStart.minusNanos(1), null, null, 0, Integer.MAX_VALUE);
        List<Transaction> priorTransactions = loadTransactionPort.loadAllByQuery(priorQuery);
        BigDecimal openingBalance = computeBalance(priorTransactions);

        // Current month transactions
        TransactionQuery monthQuery = new TransactionQuery(userId, monthStart, monthEnd, null, null, 0, Integer.MAX_VALUE);
        List<Transaction> monthTransactions = loadTransactionPort.loadAllByQuery(monthQuery);

        BigDecimal totalIncome = sum(monthTransactions, TransactionType.INCOME);
        BigDecimal totalExpenses = sum(monthTransactions, TransactionType.EXPENSE);
        BigDecimal totalAllocations = sum(monthTransactions, TransactionType.GOAL_ALLOCATION);
        BigDecimal totalWithdrawals = sum(monthTransactions, TransactionType.GOAL_WITHDRAWAL);

        BigDecimal closingBalance = openingBalance
                .add(totalIncome)
                .add(totalWithdrawals)
                .subtract(totalExpenses)
                .subtract(totalAllocations);

        Map<String, BigDecimal> spendingByCategory = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().name(),
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        List<TransactionResponse> transactions = monthTransactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new MonthlyStatementResponse(
                month, year,
                openingBalance, totalIncome, totalExpenses, totalAllocations, totalWithdrawals,
                closingBalance, spendingByCategory, transactions
        );
    }

    private BigDecimal computeBalance(List<Transaction> transactions) {
        BigDecimal income = sum(transactions, TransactionType.INCOME);
        BigDecimal expenses = sum(transactions, TransactionType.EXPENSE);
        BigDecimal allocations = sum(transactions, TransactionType.GOAL_ALLOCATION);
        BigDecimal withdrawals = sum(transactions, TransactionType.GOAL_WITHDRAWAL);
        return income.add(withdrawals).subtract(expenses).subtract(allocations);
    }

    private BigDecimal sum(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getAmount(), t.getDescription(), t.getType(), t.getCategory(),
                t.getDate(), t.getCreatedAt(), t.isRecurring(), t.getFrequency(), t.getRepeatDay(),
                t.getIconKey(), t.getGoalId(), t.getTotalInstallments(), t.getCurrentInstallment()
        );
    }
}
