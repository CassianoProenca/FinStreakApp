package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de uma meta financeira")
public record GoalResponse(

        @Schema(description = "Identificador único da meta", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Nome do objetivo financeiro", example = "Viagem Japão")
        String title,

        @Schema(description = "Valor total necessário para atingir a meta", example = "15000.00")
        BigDecimal targetAmount,

        @Schema(description = "Valor já acumulado na meta até o momento", example = "3750.00")
        BigDecimal currentAmount,

        @Schema(description = "Percentual de progresso em relação ao valor alvo (0 a 100)", example = "25.0")
        Double progressPercentage,

        @Schema(description = "Data limite para atingir a meta", example = "2027-01-01T00:00:00")
        LocalDateTime deadline,

        @Schema(description = "Status atual da meta", example = "IN_PROGRESS",
                allowableValues = {"IN_PROGRESS", "COMPLETED", "CANCELLED"})
        GoalStatus status,

        @Schema(description = "Chave do ícone visual para exibição no app", example = "airplane")
        String iconKey

) {}
