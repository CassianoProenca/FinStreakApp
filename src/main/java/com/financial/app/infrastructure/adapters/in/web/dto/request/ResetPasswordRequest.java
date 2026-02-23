package com.financial.app.infrastructure.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para redefinição de senha via token")
public record ResetPasswordRequest(

        @Schema(description = "Token recebido por e-mail via /forgot-password", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        @NotBlank String token,

        @Schema(description = "Nova senha desejada (mínimo 6 caracteres)", example = "novaSenha456")
        @NotBlank @Size(min = 6) String newPassword

) {}
