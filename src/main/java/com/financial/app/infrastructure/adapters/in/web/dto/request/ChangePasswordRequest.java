package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para troca de senha do usuário autenticado")
public record ChangePasswordRequest(

        @Schema(description = "Senha atual do usuário", example = "senhaAtual123")
        @NotBlank String oldPassword,

        @Schema(description = "Nova senha desejada (mínimo 6 caracteres)", example = "novaSenha456")
        @NotBlank @Size(min = 6) String newPassword

) {}
