package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.GetNotificationsUseCase;
import com.financial.app.application.ports.in.MarkNotificationReadUseCase;
import com.financial.app.infrastructure.adapters.in.web.dto.response.NotificationResponse;
import com.financial.app.infrastructure.adapters.out.persistence.JpaNotificationAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Histórico de notificações do usuário")
public class NotificationController {

    private final GetNotificationsUseCase getNotificationsUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final JpaNotificationAdapter notificationAdapter;

    @Operation(summary = "Listar Notificações", description = "Retorna todas as notificações do usuário ordenadas da mais recente para a mais antiga.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de notificações retornada com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            })
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<NotificationResponse> responses = getNotificationsUseCase.execute(userId).stream()
                .map(n -> new NotificationResponse(n.getId(), titleForType(n.getType()), n.getMessage(), n.getType(), n.isRead(), n.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    private String titleForType(com.financial.app.domain.model.enums.NotificationType type) {
        return switch (type) {
            case STREAK -> "Sequência Aumentada! 🔥";
            case LEVEL_UP -> "Subiu de Nível! 🆙";
            case ACHIEVEMENT -> "Nova Medalha! 🏆";
            case GOAL_COMPLETED -> "Meta Concluída! 🎯";
            case BUDGET_ALERT -> "Alerta de Orçamento! ⚠️";
            case SYSTEM -> "Notificação do Sistema";
        };
    }

    @Operation(summary = "Contagem de não lidas", description = "Retorna a quantidade de notificações não lidas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            })
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        long count = notificationAdapter.countUnread(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @Operation(summary = "Marcar como Lida", description = "Marca uma notificação específica como lida.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Notificação marcada como lida"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "A notificação não pertence ao usuário autenticado"),
                    @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
            })
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID único da notificação", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        markNotificationReadUseCase.execute(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Marcar todas como lidas", description = "Marca todas as notificações do usuário como lidas.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Todas as notificações marcadas como lidas"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            })
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        markNotificationReadUseCase.markAll(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir notificação", description = "Remove permanentemente uma notificação.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Notificação removida com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "A notificação não pertence ao usuário autenticado"),
                    @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID único da notificação", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        markNotificationReadUseCase.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
