package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.UpdateUserPreferencesUseCase;
import com.financial.app.application.ports.in.command.UpdateUserPreferencesCommand;
import com.financial.app.infrastructure.adapters.in.web.dto.request.UpdateSettingsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Tag(name = "Configurações", description = "Preferências do usuário")
public class SettingsController {

    private final UpdateUserPreferencesUseCase updateUserPreferencesUseCase;

    @Operation(
            summary = "Atualizar preferências",
            description = "Altera o tema visual (LIGHT/DARK) e a configuração de notificações push do usuário.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Preferências atualizadas com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (tema inválido ou campo ausente)"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @PatchMapping
    public ResponseEntity<Void> updateSettings(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novas preferências do usuário",
                    content = @Content(examples = @ExampleObject(value = "{\"theme\": \"DARK\", \"notificationsEnabled\": true}"))
            )
            @Valid UpdateSettingsRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        UpdateUserPreferencesCommand command = new UpdateUserPreferencesCommand(
                userId,
                request.theme(),
                request.notificationsEnabled()
        );

        updateUserPreferencesUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
