package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.GetNotificationsUseCase;
import com.financial.app.application.ports.in.MarkNotificationReadUseCase;
import com.financial.app.infrastructure.adapters.in.web.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Listar Notificações", description = "Retorna as notificações do usuário, mais recentes primeiro.")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<NotificationResponse> responses = getNotificationsUseCase.execute(userId).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(), n.getType(), n.isRead(), n.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Marcar como Lida", description = "Marca uma notificação específica como lida.")
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id, Authentication authentication) {
        markNotificationReadUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
