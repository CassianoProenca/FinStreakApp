package com.financial.app.services;

import com.financial.app.dto.request.CreateGoalRequest;
import com.financial.app.dto.request.CreateTransactionRequest;
import com.financial.app.dto.request.OnboardingRequest;
import com.financial.app.model.User;
import com.financial.app.model.enums.TransactionCategory;
import com.financial.app.model.enums.TransactionType;
import com.financial.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final GoalService goalService;

    @Transactional
    public void completeOnboarding(UUID userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isOnboardingCompleted()) {
            throw new IllegalStateException("Onboarding already completed for this user");
        }

        // 1. Criar transação de saldo inicial se > 0
        if (request.initialBalance() != null && request.initialBalance().compareTo(BigDecimal.ZERO) > 0) {
            CreateTransactionRequest trxRequest = new CreateTransactionRequest(
                    request.initialBalance(),
                    "Initial Balance",
                    TransactionType.INCOME,
                    TransactionCategory.OTHER,
                    LocalDateTime.now()
            );
            transactionService.create(userId, trxRequest);
        }

        // 2. Criar meta inicial se informado
        if (request.goalName() != null && !request.goalName().isBlank()) {
            // Define uma meta fictícia de exemplo para iniciar
            CreateGoalRequest goalRequest = new CreateGoalRequest(
                    request.goalName(),
                    BigDecimal.valueOf(1000), // Valor alvo simbólico
                    BigDecimal.ZERO,
                    LocalDate.now().plusMonths(1), // Prazo de 1 mês
                    "STAR" // Ícone padrão
            );
            goalService.create(userId, goalRequest);
        }

        // 3. Atualizar flag do usuário
        user.setOnboardingCompleted(true);
        userRepository.save(user);
    }
}
