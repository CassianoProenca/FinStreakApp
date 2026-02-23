package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação")
public record LoginRequest(

        @Schema(description = "E-mail cadastrado", example = "lucas@example.com")
        @NotBlank @Email String email,

        @Schema(description = "Senha do usuário (mínimo 6 caracteres)", example = "senha123")
        @NotBlank String password

) {}
