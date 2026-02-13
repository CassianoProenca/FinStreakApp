package com.financial.app.application.usecase;

import com.financial.app.application.ports.out.CheckTransactionInstancePort;
import com.financial.app.application.ports.out.LoadRecurringTransactionsPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurrenceServiceTest {

    @Mock private LoadRecurringTransactionsPort loadPort;
    @Mock private CheckTransactionInstancePort checkPort;
    @Mock private SaveTransactionPort savePort;

    @InjectMocks
    private ProcessRecurringTransactionsService service;

    private Transaction recurringTransaction;

    @BeforeEach
    void setUp() {
        recurringTransaction = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .amount(new java.math.BigDecimal("1500"))
                .description("Rent")
                .isRecurring(true)
                .repeatDay(10)
                .build();
    }

    @Test
    @DisplayName("Should create new instance if it's past repeat day and instance doesn't exist")
    void shouldCreateInstance() {
        when(loadPort.loadActiveRecurring()).thenReturn(List.of(recurringTransaction));
        // Simular que hoje é dia 15 (passou do dia 10)
        when(checkPort.existsInstanceInPeriod(any(), any(), any())).thenReturn(false);

        service.execute();

        verify(savePort, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should NOT create instance if it already exists for the month")
    void shouldNotDuplicateInstance() {
        when(loadPort.loadActiveRecurring()).thenReturn(List.of(recurringTransaction));
        when(checkPort.existsInstanceInPeriod(any(), any(), any())).thenReturn(true);

        service.execute();

        verify(savePort, never()).save(any());
    }
}
