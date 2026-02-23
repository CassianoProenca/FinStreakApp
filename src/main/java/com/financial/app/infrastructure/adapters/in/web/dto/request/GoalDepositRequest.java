package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Dados para depósito ou saque em uma meta financeira")
public record GoalDepositRequest(

        @Schema(description = "Valor a ser depositado ou sacado em R$", example = "500.00")
        @NotNull @Positive BigDecimal amount,

        @Schema(description = "Descrição opcional da movimentação", example = "Aporte mensal de fevereiro")
        String description

) {}
