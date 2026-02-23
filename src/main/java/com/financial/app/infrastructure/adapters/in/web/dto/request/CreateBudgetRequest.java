package com.financial.app.infrastructure.adapters.in.web.dto.request;

import com.financial.app.domain.model.enums.TransactionCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Dados para definir ou atualizar um limite de gasto por categoria")
public record CreateBudgetRequest(

        @Schema(description = "Categoria de gasto a ser limitada", example = "FOOD",
                allowableValues = {"FOOD", "TRANSPORT", "HOUSING", "UTILITIES", "LEISURE", "EDUCATION", "HEALTH", "SHOPPING", "OTHER"})
        @NotNull TransactionCategory category,

        @Schema(description = "Valor máximo de gasto permitido para a categoria no período", example = "800.00")
        @NotNull @Positive BigDecimal limitAmount,

        @Schema(description = "Mês de referência (1-12). Se não informado, usa o mês atual", example = "2")
        Integer month,

        @Schema(description = "Ano de referência. Se não informado, usa o ano atual", example = "2026")
        Integer year

) {}
