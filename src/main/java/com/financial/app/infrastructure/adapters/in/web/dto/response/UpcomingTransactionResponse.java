package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Transação futura (parcela ou projeção recorrente)")
public record UpcomingTransactionResponse(

        @Schema(description = "ID da transação (ou da transação-pai para projeções)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Valor", example = "150.00")
        BigDecimal amount,

        @Schema(description = "Descrição", example = "Streaming - parcela 2/12")
        String description,

        @Schema(description = "Tipo da transação", example = "EXPENSE")
        TransactionType type,

        @Schema(description = "Categoria", example = "LEISURE")
        TransactionCategory category,

        @Schema(description = "Data prevista", example = "2026-03-15T10:00:00")
        LocalDateTime date,

        @Schema(description = "Data de criação (nula para projeções)")
        LocalDateTime createdAt,

        @Schema(description = "Indica se é recorrente", example = "false")
        boolean isRecurring,

        @Schema(description = "Frequência da recorrência", example = "MONTHLY")
        String frequency,

        @Schema(description = "Dia do mês para repetição", example = "15")
        Integer repeatDay,

        @Schema(description = "Chave do ícone", example = "tv")
        String iconKey,

        @Schema(description = "ID da meta vinculada")
        UUID goalId,

        @Schema(description = "Total de parcelas", example = "12")
        Integer totalInstallments,

        @Schema(description = "Número desta parcela", example = "2")
        Integer currentInstallment,

        @Schema(description = "Indica se é uma projeção (não salva no DB) ou uma parcela real", example = "false")
        boolean isProjection

) {}
