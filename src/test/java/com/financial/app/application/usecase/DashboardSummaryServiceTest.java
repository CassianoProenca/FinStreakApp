package com.financial.app.application.usecase;

import com.financial.app.application.ports.out.BudgetPort;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.response.DashboardSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardSummaryServiceTest {

    @Mock private LoadTransactionPort loadTransactionPort;
    @Mock private LoadGamificationProfilePort loadGamificationProfilePort;
    @Mock private BudgetPort budgetPort;
    @Mock private LoadAchievementsPort loadAchievementsPort;

    @InjectMocks
    private GetDashboardSummaryService service;

    @Test
    @DisplayName("Should correctly sum income and expenses for the dashboard")
    void shouldCalculateDashboardCorrectly() {
        UUID userId = UUID.randomUUID();
        
        List<Transaction> transactions = List.of(
                Transaction.builder().amount(new BigDecimal("5000")).type(TransactionType.INCOME).category(TransactionCategory.SALARY).build(),
                Transaction.builder().amount(new BigDecimal("1500")).type(TransactionType.EXPENSE).category(TransactionCategory.HOUSING).build(),
                Transaction.builder().amount(new BigDecimal("500")).type(TransactionType.EXPENSE).category(TransactionCategory.FOOD).build()
        );

        when(loadTransactionPort.loadAllByQuery(any())).thenReturn(transactions);
        when(loadGamificationProfilePort.loadByUserId(userId)).thenReturn(Optional.empty());
        when(budgetPort.findByUserAndPeriod(any(), anyInt(), anyInt())).thenReturn(List.of());
        when(loadAchievementsPort.loadByUserId(userId)).thenReturn(List.of());

        DashboardSummaryResponse summary = service.execute(userId, 2, 2026);

        assertEquals(new BigDecimal("5000"), summary.totalIncome());
        assertEquals(new BigDecimal("2000"), summary.totalExpenses());
        assertEquals(new BigDecimal("3000"), summary.balance());
        assertEquals(2, summary.spendingByCategory().size());
    }
}
