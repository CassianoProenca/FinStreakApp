package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.UpdateUserProfileUseCase;
import com.financial.app.application.ports.in.command.UpdateUserProfileCommand;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.in.web.dto.request.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de perfil do usuário")
public class UserController {

    private final UpdateUserProfileUseCase updateUserProfileUseCase;

    @Operation(
        summary = "Atualizar perfil",
        description = "Permite alterar o nome, senha e foto de perfil do usuário logado. Todos os campos são opcionais — envie apenas os que deseja alterar.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (senha com menos de 6 caracteres)"),
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

        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
                userId,
                request.name(),
                request.password(),
                request.avatarUrl()
        );

        updateUserProfileUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
}
