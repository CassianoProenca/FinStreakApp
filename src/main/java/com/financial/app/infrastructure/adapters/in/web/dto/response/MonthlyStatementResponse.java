package com.financial.app.infrastructure.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Schema(description = "Extrato financeiro de um mês específico")
public record MonthlyStatementResponse(

        @Schema(description = "Mês do extrato (1-12)", example = "3")
        int month,

        @Schema(description = "Ano do extrato", example = "2026")
        int year,

        @Schema(description = "Saldo acumulado até o último dia do mês anterior", example = "1000.00")
        BigDecimal openingBalance,

        @Schema(description = "Total de receitas no mês", example = "5000.00")
        BigDecimal totalIncome,

        @Schema(description = "Total de despesas no mês", example = "3000.00")
        BigDecimal totalExpenses,

        @Schema(description = "Total alocado em metas no mês", example = "200.00")
        BigDecimal totalAllocations,

        @Schema(description = "Total resgatado de metas no mês", example = "0.00")
        BigDecimal totalWithdrawals,

        @Schema(description = "Saldo ao final do mês (openingBalance + income + withdrawals - expenses - allocations)", example = "1800.00")
        BigDecimal closingBalance,

        @Schema(description = "Gastos agrupados por categoria no mês")
        Map<String, BigDecimal> spendingByCategory,

        @Schema(description = "Lista de todas as transações do mês em ordem cronológica")
        List<TransactionResponse> transactions

) {}
