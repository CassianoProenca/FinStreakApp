package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.UpdateUserProfileUseCase;
import com.financial.app.application.ports.in.command.UpdateUserProfileCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.in.web.dto.request.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de perfil do usuário")
public class UserController {

    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    @Operation(
        summary = "Obter perfil do usuário",
        description = "Retorna os dados atualizados do perfil do usuário autenticado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "",
                "onboardingCompleted", user.isOnboardingCompleted(),
                "monthlyIncome", user.getMonthlyIncome() != null ? user.getMonthlyIncome() : BigDecimal.ZERO
        ));
    }

    @Operation(
        summary = "Atualizar perfil",
        description = "Permite alterar o nome e foto de perfil do usuário logado. Para alterar senha use POST /api/auth/change-password.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
        }
    )
    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Campos do perfil a serem atualizados (envie apenas o que mudar)",
                content = @Content(examples = @ExampleObject(value = "{\"name\": \"Lucas Oliveira\", \"avatarUrl\": \"https://cdn.example.com/avatars/lucas.png\"}"))
            )
            @Valid UpdateProfileRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(userId, request.name(), null, request.avatarUrl());
        updateUserProfileUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Atualizar renda mensal",
        description = "Permite ao usuário atualizar sua renda mensal após o onboarding. (#25)",
        responses = {
            @ApiResponse(responseCode = "204", description = "Renda atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Valor inválido"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )
    @PatchMapping("/me/income")
    public ResponseEntity<Void> updateMonthlyIncome(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(value = "{\"monthlyIncome\": 6500.00}"))
            )
            @Valid @RequestBody UpdateMonthlyIncomeRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        user.setMonthlyIncome(request.monthlyIncome());
        saveUserPort.save(user);
        return ResponseEntity.noContent().build();
    }

    public record UpdateMonthlyIncomeRequest(
            @Positive(message = "Monthly income must be positive") BigDecimal monthlyIncome
    ) {}
}
