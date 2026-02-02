package com.financial.app.services;

import com.financial.app.dto.request.OnboardingRequest;
import com.financial.app.model.Goal;
import com.financial.app.model.Transaction;
import com.financial.app.model.User;
import com.financial.app.model.enums.GoalStatus;
import com.financial.app.model.enums.TransactionCategory;
import com.financial.app.model.enums.TransactionType;
import com.financial.app.repositories.GoalRepository;
import com.financial.app.repositories.TransactionRepository;
import com.financial.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public void completeOnboarding(UUID userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isOnboardingCompleted()) {
            throw new IllegalStateException("Onboarding already completed for this user");
        }

        // 1. Renda Mensal (Income)
        if (request.monthlyIncome() != null && request.monthlyIncome().compareTo(BigDecimal.ZERO) > 0) {
            Transaction income = Transaction.builder()
                    .userId(userId)
                    .amount(request.monthlyIncome())
                    .description("Renda Mensal Inicial")
                    .type(TransactionType.INCOME)
                    .category(TransactionCategory.SALARY)
                    .date(LocalDateTime.now())
                    .build();
            transactionRepository.save(income);
        }

        // 2. Despesas Fixas (Fixed Expenses)
        if (request.fixedExpenses() != null && !request.fixedExpenses().isEmpty()) {
            List<Transaction> expenses = new ArrayList<>();
            for (OnboardingRequest.ExpenseRequest expReq : request.fixedExpenses()) {
                Transaction expense = Transaction.builder()
                        .userId(userId)
                        .amount(expReq.amount())
                        .description(expReq.name())
                        .type(TransactionType.EXPENSE)
                        .category(resolveCategory(expReq.category()))
                        .date(LocalDateTime.now())
                        .build();
                expenses.add(expense);
            }
            transactionRepository.saveAll(expenses);
        }

        // 3. Meta Principal (Main Goal)
        if (request.mainGoal() != null) {
            Goal goal = Goal.builder()
                    .userId(userId)
                    .title(request.mainGoal().title())
                    .targetAmount(request.mainGoal().targetAmount())
                    .currentAmount(BigDecimal.ZERO)
                    .deadline(request.mainGoal().deadline())
                    .status(GoalStatus.IN_PROGRESS)
                    .icon("STAR") // Padrão solicitado
                    .build();
            goalRepository.save(goal);
        }

        // 4. Finalização
        user.setOnboardingCompleted(true);
        userRepository.save(user);
    }

    private TransactionCategory resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return TransactionCategory.OTHER;
        }
        try {
            return TransactionCategory.valueOf(categoryName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TransactionCategory.OTHER;
        }
    }
}