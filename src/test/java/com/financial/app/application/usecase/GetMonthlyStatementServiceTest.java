package com.financial.app.application.usecase;

import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.response.MonthlyStatementResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMonthlyStatementServiceTest {

    @Mock
    private LoadTransactionPort loadTransactionPort;

    @InjectMocks
    private GetMonthlyStatementService service;

    @Test
    @DisplayName("Should calculate opening balance from prior transactions")
    void shouldComputeOpeningBalanceFromPriorTransactions() {
        UUID userId = UUID.randomUUID();

        List<Transaction> priorTxs = List.of(
                tx(TransactionType.INCOME, "1000"),
                tx(TransactionType.EXPENSE, "300")
        );
        List<Transaction> monthTxs = List.of(
                tx(TransactionType.INCOME, "5000"),
                tx(TransactionType.EXPENSE, "1200")
        );

        // Prior query: endDate before month start
        when(loadTransactionPort.loadAllByQuery(argThat(q -> q != null && q.endDate() != null && q.startDate() == null)))
                .thenReturn(priorTxs);
        // Month query: both startDate and endDate set
        when(loadTransactionPort.loadAllByQuery(argThat(q -> q != null && q.startDate() != null && q.endDate() != null)))
                .thenReturn(monthTxs);

        MonthlyStatementResponse result = service.execute(userId, 2, 2026);

        assertEquals(new BigDecimal("700"), result.openingBalance());  // 1000 - 300
        assertEquals(new BigDecimal("5000"), result.totalIncome());
        assertEquals(new BigDecimal("1200"), result.totalExpenses());
        assertEquals(BigDecimal.ZERO, result.totalAllocations());
        assertEquals(BigDecimal.ZERO, result.totalWithdrawals());
        // closingBalance = 700 + 5000 - 1200 = 4500
        assertEquals(new BigDecimal("4500"), result.closingBalance());
        assertEquals(2, result.month());
        assertEquals(2026, result.year());
        assertEquals(2, result.transactions().size());
    }

    @Test
    @DisplayName("Should group spending by category")
    void shouldGroupSpendingByCategory() {
        UUID userId = UUID.randomUUID();

        when(loadTransactionPort.loadAllByQuery(argThat(q -> q != null && q.startDate() == null))).thenReturn(List.of());

        List<Transaction> monthTxs = List.of(
                txWithCategory(TransactionType.EXPENSE, "500", TransactionCategory.FOOD),
                txWithCategory(TransactionType.EXPENSE, "150", TransactionCategory.TRANSPORT),
                txWithCategory(TransactionType.EXPENSE, "100", TransactionCategory.FOOD)
        );
        when(loadTransactionPort.loadAllByQuery(argThat(q -> q != null && q.startDate() != null))).thenReturn(monthTxs);

        MonthlyStatementResponse result = service.execute(userId, 2, 2026);

        assertEquals(new BigDecimal("600"), result.spendingByCategory().get("FOOD"));
        assertEquals(new BigDecimal("150"), result.spendingByCategory().get("TRANSPORT"));
        assertFalse(result.spendingByCategory().containsKey("INCOME"));
    }

    @Test
    @DisplayName("Should include allocations and withdrawals in balance")
    void shouldHandleGoalAllocationsAndWithdrawals() {
        UUID userId = UUID.randomUUID();

        when(loadTransactionPort.loadAllByQuery(argThat(q -> q != null && q.startDate() == null))).thenReturn(List.of());

        List<Transaction> monthTxs = List.of(
                tx(TransactionType.INCOME, "2000"),
                tx(TransactionType.GOAL_ALLOCATION, "500"),
                tx(TransactionType.GOAL_WITHDRAWAL, "200")
        );
        when(loadTransactionPort.loadAllByQuery(argThat(q -> q != null && q.startDate() != null))).thenReturn(monthTxs);

        MonthlyStatementResponse result = service.execute(userId, 2, 2026);

        assertEquals(new BigDecimal("500"), result.totalAllocations());
        assertEquals(new BigDecimal("200"), result.totalWithdrawals());
        // closingBalance = 0 + 2000 + 200 - 0 - 500 = 1700
        assertEquals(new BigDecimal("1700"), result.closingBalance());
    }

    private Transaction tx(TransactionType type, String amount) {
        return Transaction.builder()
                .amount(new BigDecimal(amount))
                .type(type)
                .category(TransactionCategory.OTHER)
                .date(LocalDateTime.now())
                .build();
    }

    private Transaction txWithCategory(TransactionType type, String amount, TransactionCategory category) {
        return Transaction.builder()
                .amount(new BigDecimal(amount))
                .type(type)
                .category(category)
                .date(LocalDateTime.now())
                .build();
    }
}
