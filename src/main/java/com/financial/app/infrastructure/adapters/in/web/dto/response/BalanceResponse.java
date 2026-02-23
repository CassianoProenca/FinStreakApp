package com.financial.app.infrastructure.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Resumo do saldo financeiro acumulado desde o início")
public record BalanceResponse(

        @Schema(description = "Soma de todas as receitas registradas (INCOME)", example = "15000.00")
        BigDecimal totalIncome,

        @Schema(description = "Soma de todas as despesas registradas (EXPENSE)", example = "8500.00")
        BigDecimal totalExpenses,

        @Schema(description = "Saldo líquido disponível para gasto = Receitas + Resgates de Metas - Despesas - Depósitos em Metas", example = "5200.00")
        BigDecimal availableBalance,

        @Schema(description = "Patrimônio total = Saldo Disponível + Total alocado em Metas. Representa a riqueza real do usuário", example = "7500.00")
        BigDecimal totalEquity

) {}
