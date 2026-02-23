package com.financial.app.infrastructure.adapters.in.web.dto.request;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados para registro de uma transação financeira")
public record CreateTransactionRequest(

        @Schema(description = "Valor da transação em R$", example = "45.90")
        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal amount,

        @Schema(description = "Descrição da transação", example = "iFood - Almoço")
        @NotBlank(message = "A descrição não pode estar vazia")
        @Size(min = 3, max = 100, message = "A descrição deve ter entre 3 e 100 caracteres")
        String description,

        @Schema(description = "Tipo da transação. Use INCOME para receitas, EXPENSE para despesas", example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE", "GOAL_ALLOCATION", "GOAL_WITHDRAWAL"})
        @NotNull(message = "O tipo da transação é obrigatório")
        TransactionType type,

        @Schema(description = "Categoria da transação", example = "FOOD",
                allowableValues = {"FOOD", "TRANSPORT", "HOUSING", "UTILITIES", "LEISURE", "EDUCATION", "HEALTH", "SHOPPING", "SALARY", "FREELANCE", "INVESTMENT", "OTHER"})
        @NotNull(message = "A categoria é obrigatória")
        TransactionCategory category,

        @Schema(description = "Data e hora da transação (ISO 8601). Se não informado, assume o momento atual", example = "2026-02-13T14:00:00")
        LocalDateTime date,

        @Schema(description = "Indica se a transação se repete periodicamente", example = "false")
        boolean isRecurring,

        @Schema(description = "Frequência da recorrência. Ex: MONTHLY, WEEKLY. Obrigatório se isRecurring=true", example = "MONTHLY")
        String frequency,

        @Schema(description = "Dia do mês para repetição (1-31). Usado quando frequency=MONTHLY", example = "5")
        Integer repeatDay,

        @Schema(description = "Chave do ícone visual para exibição no app", example = "hamburger")
        String iconKey

) {}
