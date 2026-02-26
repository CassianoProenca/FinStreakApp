package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de uma transação financeira")
public record TransactionResponse(

        @Schema(description = "Identificador único da transação", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Valor da transação em R$", example = "45.90")
        BigDecimal amount,

        @Schema(description = "Descrição da transação", example = "iFood - Almoço")
        String description,

        @Schema(description = "Tipo da transação", example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE", "GOAL_ALLOCATION", "GOAL_WITHDRAWAL"})
        TransactionType type,

        @Schema(description = "Categoria da transação", example = "FOOD")
        TransactionCategory category,

        @Schema(description = "Data e hora em que a transação ocorreu", example = "2026-02-13T14:00:00")
        LocalDateTime date,

        @Schema(description = "Data e hora em que o registro foi criado no sistema", example = "2026-02-13T14:05:00")
        LocalDateTime createdAt,

        @Schema(description = "Indica se a transação é recorrente", example = "false")
        boolean isRecurring,

        @Schema(description = "Frequência da recorrência (ex: MONTHLY, WEEKLY). Nulo se não recorrente", example = "MONTHLY")
        String frequency,

        @Schema(description = "Dia do mês para repetição (1-31). Nulo se não recorrente", example = "5")
        Integer repeatDay,

        @Schema(description = "Chave do ícone visual para exibição no app", example = "hamburger")
        String iconKey,

        @Schema(description = "ID da meta vinculada a esta transação (apenas para aportes/resgates)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID goalId,

        @Schema(description = "Número total de parcelas (nulo se não parcelado)", example = "12")
        Integer totalInstallments,

        @Schema(description = "Número da parcela atual (nulo se não parcelado)", example = "1")
        Integer currentInstallment

) {}
