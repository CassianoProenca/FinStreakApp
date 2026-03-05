package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Notificação gerada pelo sistema para o usuário")
public record NotificationResponse(

        @Schema(description = "Identificador único da notificação", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Título amigável da notificação", example = "Nova Medalha! 🏆")
        String title,

        @Schema(description = "Texto da mensagem da notificação", example = "Parabéns! Você atingiu um streak de 7 dias!")
        String message,

        @Schema(description = "Tipo da notificação que indica o evento gerador",
                example = "ACHIEVEMENT",
                allowableValues = {"STREAK", "LEVEL_UP", "ACHIEVEMENT", "GOAL_COMPLETED", "BUDGET_ALERT", "SYSTEM"})
        NotificationType type,

        @Schema(description = "Indica se a notificação já foi lida pelo usuário", example = "false")
        boolean isRead,

        @Schema(description = "Data e hora em que a notificação foi criada", example = "2026-02-20T10:30:00")
        LocalDateTime createdAt

) {}
