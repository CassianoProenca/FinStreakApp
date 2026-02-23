package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.GetNotificationsUseCase;
import com.financial.app.application.ports.in.MarkNotificationReadUseCase;
import com.financial.app.infrastructure.adapters.in.web.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Histórico de notificações do usuário")
public class NotificationController {

    private final GetNotificationsUseCase getNotificationsUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;

    @Operation(
            summary = "Listar Notificações",
            description = "Retorna todas as notificações do usuário ordenadas da mais recente para a mais antiga. Tipos: STREAK (ofensiva), LEVEL_UP (subiu de nível), ACHIEVEMENT (conquista desbloqueada).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de notificações retornada com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<NotificationResponse> responses = getNotificationsUseCase.execute(userId).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(), n.getType(), n.isRead(), n.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Marcar como Lida",
            description = "Marca uma notificação específica como lida. Use para atualizar o badge de notificações não lidas no app.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Notificação marcada como lida"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
            }
    )
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID único da notificação", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            Authentication authentication) {
        markNotificationReadUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
