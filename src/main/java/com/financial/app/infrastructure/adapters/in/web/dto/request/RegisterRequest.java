package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de uma nova conta")
public record RegisterRequest(

        @Schema(description = "Nome completo do usuário", example = "Lucas Silva")
        @NotBlank String name,

        @Schema(description = "E-mail único para login", example = "lucas@example.com")
        @NotBlank @Email String email,

        @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "senha123")
        @NotBlank @Size(min = 6) String password

) {}
