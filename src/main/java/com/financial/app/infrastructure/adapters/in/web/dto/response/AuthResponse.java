package com.financial.app.infrastructure.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Resposta de autenticação com token JWT e dados básicos do perfil")
public record AuthResponse(

        @Schema(description = "Token JWT para ser usado no header Authorization: Bearer <token>",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Nome de exibição do usuário", example = "Lucas Silva")
        String name,

        @Schema(description = "URL da foto de perfil do usuário", example = "https://cdn.example.com/avatars/lucas.png")
        String avatarUrl,

        @Schema(description = "Indica se o usuário já completou o fluxo de onboarding. Se false, redirecionar para /onboarding/complete", example = "true")
        boolean onboardingCompleted,

        @Schema(description = "Renda mensal cadastrada pelo usuário. Null se onboarding não foi concluído", example = "5000.00")
        BigDecimal monthlyIncome

) {}
