package com.financial.app.application.usecase;

import com.financial.app.application.ports.out.LoadRecurringTransactionsPort;
import com.financial.app.application.ports.out.LoadUpcomingTransactionsPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.response.UpcomingTransactionResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUpcomingTransactionsServiceTest {

    @Mock
    private LoadUpcomingTransactionsPort loadUpcomingTransactionsPort;

    @Mock
    private LoadRecurringTransactionsPort loadRecurringTransactionsPort;

    @InjectMocks
    private GetUpcomingTransactionsService service;

    @Test
    @DisplayName("Should return future installments as non-projection")
    void shouldReturnFutureInstallments() {
        UUID userId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        Transaction child = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .amount(new BigDecimal("100"))
                .description("Parcela 2/3")
                .type(TransactionType.EXPENSE)
                .category(TransactionCategory.LEISURE)
                .date(LocalDateTime.now().plusMonths(1))
                .parentTransactionId(parentId)
                .totalInstallments(3)
                .currentInstallment(2)
                .build();

        when(loadUpcomingTransactionsPort.loadFutureInstallments(eq(userId), any())).thenReturn(List.of(child));
        when(loadRecurringTransactionsPort.loadActiveRecurring()).thenReturn(List.of());

        List<UpcomingTransactionResponse> result = service.execute(userId);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isProjection());
        assertEquals(2, result.get(0).currentInstallment());
        assertEquals(3, result.get(0).totalInstallments());
    }

    @Test
    @DisplayName("Should return 3 projections for a recurring transaction")
    void shouldReturn3ProjectionsForRecurring() {
        UUID userId = UUID.randomUUID();

        Transaction recurring = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .amount(new BigDecimal("50"))
                .description("Netflix")
                .type(TransactionType.EXPENSE)
                .category(TransactionCategory.LEISURE)
                .date(LocalDateTime.now().minusMonths(1))
                .isRecurring(true)
                .frequency("MONTHLY")
                .repeatDay(15)
                .build();

        when(loadUpcomingTransactionsPort.loadFutureInstallments(eq(userId), any())).thenReturn(List.of());
        when(loadRecurringTransactionsPort.loadActiveRecurring()).thenReturn(List.of(recurring));

        List<UpcomingTransactionResponse> result = service.execute(userId);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(UpcomingTransactionResponse::isProjection));
        assertTrue(result.stream().allMatch(r -> r.amount().compareTo(new BigDecimal("50")) == 0));
    }

    @Test
    @DisplayName("Should not include recurring transactions from other users")
    void shouldFilterRecurringByUserId() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Transaction otherRecurring = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(otherUserId)
                .amount(new BigDecimal("30"))
                .description("Other user's subscription")
                .type(TransactionType.EXPENSE)
                .category(TransactionCategory.OTHER)
                .date(LocalDateTime.now().minusMonths(1))
                .isRecurring(true)
                .frequency("MONTHLY")
                .repeatDay(10)
                .build();

        when(loadUpcomingTransactionsPort.loadFutureInstallments(eq(userId), any())).thenReturn(List.of());
        when(loadRecurringTransactionsPort.loadActiveRecurring()).thenReturn(List.of(otherRecurring));

        List<UpcomingTransactionResponse> result = service.execute(userId);

        assertTrue(result.isEmpty());
    }
}
