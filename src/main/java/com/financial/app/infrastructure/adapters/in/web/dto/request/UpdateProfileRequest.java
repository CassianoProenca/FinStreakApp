package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização do perfil do usuário. Todos os campos são opcionais — envie apenas o que deseja alterar")
public record UpdateProfileRequest(

        @Schema(description = "Novo nome de exibição", example = "Lucas Oliveira")
        String name,

        @Schema(description = "Nova senha (mínimo 6 caracteres). Enviar apenas para trocar a senha", example = "novaSenha456")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String password,

        @Schema(description = "URL da nova foto de perfil", example = "https://cdn.example.com/avatars/lucas.png")
        String avatarUrl

) {}
