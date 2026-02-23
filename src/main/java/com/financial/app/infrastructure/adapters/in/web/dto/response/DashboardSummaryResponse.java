package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.Achievement;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Schema(description = "Resumo financeiro consolidado do mês, incluindo saldo, orçamentos, gastos por categoria e gamificação")
public record DashboardSummaryResponse(

        @Schema(description = "Soma de todas as receitas do mês (INCOME)", example = "5000.00")
        BigDecimal totalIncome,

        @Schema(description = "Soma de todas as despesas do mês (EXPENSE)", example = "2800.00")
        BigDecimal totalExpenses,

        @Schema(description = "Saldo líquido disponível = Receitas + Resgates - Despesas - Depósitos em Metas", example = "1950.00")
        BigDecimal availableBalance,

        @Schema(description = "Patrimônio total = Saldo Disponível + Total alocado em Metas", example = "3200.00")
        BigDecimal totalEquity,

        @Schema(description = "Mapa de gastos por categoria no mês. Chave = nome da categoria, Valor = total gasto",
                example = "{\"FOOD\": 320.50, \"TRANSPORT\": 180.00, \"HOUSING\": 1500.00}")
        Map<String, BigDecimal> spendingByCategory,

        @Schema(description = "Lista de orçamentos do mês com status de consumo por categoria")
        List<BudgetSummary> budgets,

        @Schema(description = "Lista de conquistas (medalhas) desbloqueadas pelo usuário no mês")
        List<Achievement> achievements,

        @Schema(description = "Número de dias consecutivos com atividade financeira registrada (streak atual)", example = "7")
        int currentStreak

) {}
