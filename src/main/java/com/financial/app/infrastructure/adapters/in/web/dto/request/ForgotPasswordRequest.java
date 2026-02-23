package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "E-mail para envio do link de redefinição de senha")
public record ForgotPasswordRequest(

        @Schema(description = "E-mail cadastrado na conta", example = "lucas@example.com")
        @NotBlank @Email String email

) {}
