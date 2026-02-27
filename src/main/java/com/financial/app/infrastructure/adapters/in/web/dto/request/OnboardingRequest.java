package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Configuração inicial da conta. Salva em uma única operação a renda mensal, despesas fixas e meta principal")
public record OnboardingRequest(

        @Schema(description = "Renda mensal líquida do usuário", example = "5000.00")
        @Positive(message = "Monthly income must be positive")
        BigDecimal monthlyIncome,

        @Schema(description = "Dia do mês em que a renda é recebida", example = "5")
        Integer monthlyIncomeDay,

        @Schema(description = "Lista de despesas fixas mensais (ex: aluguel, internet, academia)")
        @Valid
        List<ExpenseRequest> fixedExpenses,

        @Schema(description = "Meta financeira principal que o usuário quer alcançar")
        @Valid
        GoalRequest mainGoal

) {

    @Schema(description = "Despesa fixa recorrente do usuário")
    public record ExpenseRequest(

            @Schema(description = "Nome da despesa", example = "Aluguel")
            @NotBlank(message = "Expense name is required")
            String name,

            @Schema(description = "Valor mensal da despesa", example = "1500.00")
            @NotNull(message = "Amount is required")
            @Positive(message = "Amount must be positive")
            BigDecimal amount,

            @Schema(description = "Categoria da despesa", example = "HOUSING",
                    allowableValues = {"FOOD", "TRANSPORT", "HOUSING", "UTILITIES", "LEISURE", "EDUCATION", "HEALTH", "SHOPPING", "OTHER"})
            String category,

            @Schema(description = "Chave do ícone visual", example = "house")
            String iconKey

    ) {}

    @Schema(description = "Meta financeira principal do usuário")
    public record GoalRequest(

            @Schema(description = "Nome do objetivo", example = "Reserva de Emergência")
            @NotBlank(message = "Goal title is required")
            String title,

            @Schema(description = "Valor total necessário para atingir a meta", example = "10000.00")
            @NotNull(message = "Target amount is required")
            @Positive(message = "Target amount must be positive")
            BigDecimal targetAmount,

            @Schema(description = "Data limite para atingir a meta (ISO 8601, deve ser futura)", example = "2026-12-31T00:00:00")
            @NotNull(message = "Deadline is required")
            @Future(message = "Deadline must be in the future")
            LocalDateTime deadline,

            @Schema(description = "Chave do ícone visual", example = "shield")
            String iconKey

    ) {}
}
