package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.AchievementType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Medalha ou conquista desbloqueada pelo usuário")
public record AchievementResponse(

        @Schema(description = "Identificador único da conquista", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Tipo da conquista",
                example = "STREAK_7",
                allowableValues = {"FIRST_STEPS", "STREAK_7", "STREAK_30"})
        AchievementType type,

        @Schema(description = "Nome de exibição da conquista", example = "Uma Semana de Foco")
        String name,

        @Schema(description = "Descrição do critério para desbloquear a conquista", example = "Mantenha um streak de 7 dias consecutivos")
        String description,

        @Schema(description = "Data e hora em que a conquista foi desbloqueada", example = "2026-02-20T10:30:00")
        LocalDateTime earnedAt

) {}
