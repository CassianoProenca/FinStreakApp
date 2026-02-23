package com.financial.app.infrastructure.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Resumo de consumo de um orçamento por categoria no mês")
public record BudgetSummary(

        @Schema(description = "Categoria do orçamento", example = "FOOD")
        String category,

        @Schema(description = "Limite de gasto definido pelo usuário para esta categoria", example = "800.00")
        BigDecimal limitAmount,

        @Schema(description = "Total já gasto na categoria no período", example = "320.50")
        BigDecimal spentAmount,

        @Schema(description = "Valor restante disponível = limitAmount - spentAmount", example = "479.50")
        BigDecimal remainingAmount,

        @Schema(description = "Percentual do limite já utilizado (0 a 100+). Acima de 100 indica orçamento estourado", example = "40.06")
        double percentageUsed

) {}
