package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.User;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseTest {

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private SaveTransactionPort saveTransactionPort;

    @Mock
    private CheckStreakUseCase checkStreakUseCase;

    @InjectMocks
    private CreateTransactionService createTransactionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    @DisplayName("Should create transaction and trigger streak check successfully")
    void shouldCreateTransactionAndCheckStreak() {
        UUID userId = user.getId();
        CreateTransactionCommand command = new CreateTransactionCommand(
                userId,
                new BigDecimal("100.00"),
                "Grocery",
                TransactionType.EXPENSE,
                TransactionCategory.FOOD,
                LocalDateTime.now()
        );

        when(loadUserPort.loadById(userId)).thenReturn(Optional.of(user));
        when(saveTransactionPort.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = createTransactionService.execute(command);

        assertNotNull(result);
        verify(saveTransactionPort).save(any(Transaction.class));
        verify(checkStreakUseCase).execute(userId); // Verifies side effect
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        CreateTransactionCommand command = new CreateTransactionCommand(
                userId,
                BigDecimal.TEN,
                "Test",
                TransactionType.EXPENSE,
                TransactionCategory.OTHER,
                LocalDateTime.now()
        );

        when(loadUserPort.loadById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> createTransactionService.execute(command));

        verify(saveTransactionPort, never()).save(any());
        verify(checkStreakUseCase, never()).execute(any());
    }
}
