package com.financial.app.infrastructure.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Perfil de gamificação do usuário com XP, nível, streak e avatar")
public record GamificationResponse(

        @Schema(description = "Identificador único do usuário", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID userId,

        @Schema(description = "Nome de exibição do usuário", example = "Lucas Silva")
        String name,

        @Schema(description = "URL da foto de perfil", example = "https://cdn.example.com/avatars/lucas.png")
        String avatarUrl,

        @Schema(description = "Dias consecutivos com atividade financeira registrada (streak atual)", example = "12")
        Integer currentStreak,

        @Schema(description = "Maior streak já registrado pelo usuário", example = "30")
        Integer maxStreak,

        @Schema(description = "Total de XP acumulado desde o início", example = "2750")
        Long totalXp,

        @Schema(description = "Nível atual do usuário. Cada nível requer 500 XP", example = "6")
        Integer level,

        @Schema(description = "Total de XP necessário para alcançar o próximo nível", example = "3000")
        Long xpForNextLevel,

        @Schema(description = "XP acumulado dentro do nível atual (progresso da barra de XP)", example = "250")
        Long xpWithinCurrentLevel,

        @Schema(description = "Data da última atividade registrada pelo usuário", example = "2026-02-23")
        LocalDate lastActivityDate

) {}
