package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.UpdateUserPreferencesUseCase;
import com.financial.app.application.ports.in.command.UpdateUserPreferencesCommand;
import com.financial.app.infrastructure.adapters.in.web.dto.request.UpdateSettingsRequest;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Atualizar preferências", description = "Altera tema (LIGHT/DARK) e configurações de notificação.")
    @PatchMapping
    public ResponseEntity<Void> updateSettings(
            @RequestBody @Valid UpdateSettingsRequest request,
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
