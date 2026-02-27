package com.financial.app.infrastructure.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Dados de uma missão diária e status de conclusão")
public record DailyMissionResponse(

        @Schema(description = "Identificador único da missão", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Título da missão", example = "Economia de hoje")
        String title,

        @Schema(description = "Descrição detalhada do desafio", example = "Registre pelo menos 1 despesa hoje")
        String description,

        @Schema(description = "Recompensa de XP ao completar", example = "50")
        int xpReward,

        @Schema(description = "Progresso atual do usuário na missão", example = "0")
        int currentCount,

        @Schema(description = "Quantidade necessária para completar", example = "1")
        int requiredCount,

        @Schema(description = "Indica se o usuário já completou esta missão hoje", example = "false")
        boolean completed

) {}
