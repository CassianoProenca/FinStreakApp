package com.financial.app.infrastructure.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Missão diária disponível para o usuário")
public record DailyMissionResponse(

        @Schema(description = "ID da missão", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        UUID id,

        @Schema(description = "Título da missão", example = "Lançamento do Dia")
        String title,

        @Schema(description = "Descrição da missão", example = "Registre qualquer despesa hoje")
        String description,

        @Schema(description = "XP concedido ao completar", example = "20")
        int xpReward,

        @Schema(description = "Progresso atual do usuário", example = "1")
        int currentCount,

        @Schema(description = "Quantidade necessária para completar", example = "1")
        int requiredCount,

        @Schema(description = "Se o usuário já completou esta missão hoje", example = "false")
        boolean completed

) {}
