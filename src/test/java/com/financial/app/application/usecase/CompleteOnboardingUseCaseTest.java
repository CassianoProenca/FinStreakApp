package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.command.OnboardingCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.in.web.dto.request.OnboardingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteOnboardingUseCaseTest {

    @Mock private LoadUserPort loadUserPort;
    @Mock private SaveUserPort saveUserPort;
    @Mock private SaveTransactionPort saveTransactionPort;
    @Mock private SaveGoalPort saveGoalPort;

    @InjectMocks
    private CompleteOnboardingService completeOnboardingService;

    private User user;
    private OnboardingCommand command;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("New User")
                .email("new@example.com")
                .onboardingCompleted(false)
                .build();

        OnboardingRequest.ExpenseRequest expense1 = new OnboardingRequest.ExpenseRequest("Rent", new BigDecimal("1500"), "HOUSING", "house");
        OnboardingRequest.ExpenseRequest expense2 = new OnboardingRequest.ExpenseRequest("Internet", new BigDecimal("100"), "UTILITIES", "wifi");
        OnboardingRequest.ExpenseRequest expense3 = new OnboardingRequest.ExpenseRequest("Netflix", new BigDecimal("50"), "LEISURE", "play");

        OnboardingRequest.GoalRequest goalRequest = new OnboardingRequest.GoalRequest("Buy Car", new BigDecimal("50000"), LocalDateTime.now().plusYears(1), "car");

        command = new OnboardingCommand(
                user.getId(),
                new BigDecimal("5000"),
                List.of(expense1, expense2, expense3),
                goalRequest
        );
    }

    @Test
    @DisplayName("Should successfully complete onboarding creating transactions and goal")
    void shouldCompleteOnboardingSuccess() {
        when(loadUserPort.loadById(user.getId())).thenReturn(Optional.of(user));

        completeOnboardingService.execute(command);

        // Verify Income + 3 Expenses = 4 Transactions
        verify(saveTransactionPort, times(4)).save(any(Transaction.class));

        // Verify Goal creation
        verify(saveGoalPort, times(1)).save(any(Goal.class));

        // Verify User Update
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(saveUserPort).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.isOnboardingCompleted(), "User should be marked as onboarding completed");
    }

    @Test
    @DisplayName("Should throw exception if onboarding is already completed")
    void shouldThrowIfAlreadyCompleted() {
        user.setOnboardingCompleted(true);
        when(loadUserPort.loadById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> completeOnboardingService.execute(command));

        verify(saveTransactionPort, never()).save(any());
        verify(saveGoalPort, never()).save(any());
        verify(saveUserPort, never()).save(any());
    }
}
