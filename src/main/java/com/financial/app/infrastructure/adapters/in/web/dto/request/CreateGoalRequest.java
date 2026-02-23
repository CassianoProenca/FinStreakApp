package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados para criação ou atualização de uma meta financeira")
public record CreateGoalRequest(

        @Schema(description = "Nome do objetivo financeiro", example = "Viagem Japão")
        @NotBlank(message = "O título é obrigatório")
        String title,

        @Schema(description = "Valor total necessário para atingir a meta", example = "15000.00")
        @NotNull(message = "O valor alvo é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal targetAmount,

        @Schema(description = "Valor já acumulado na meta (opcional, padrão 0)", example = "2500.00")
        BigDecimal currentAmount,

        @Schema(description = "Data limite para atingir a meta (deve ser futura, ISO 8601)", example = "2027-01-01T00:00:00")
        @NotNull(message = "A data limite é obrigatória")
        @Future(message = "A data limite deve ser no futuro")
        LocalDateTime deadline,

        @Schema(description = "Chave do ícone visual para exibição no app", example = "airplane")
        String iconKey

) {}
