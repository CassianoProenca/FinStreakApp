package com.financial.app.infrastructure.adapters.in.web.dto.request;

import com.financial.app.domain.model.enums.Theme;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Preferências visuais e de notificação do usuário")
public record UpdateSettingsRequest(

        @Schema(description = "Tema da interface do aplicativo", example = "DARK", allowableValues = {"LIGHT", "DARK"})
        @NotNull Theme theme,

        @Schema(description = "Habilita ou desabilita o recebimento de notificações push", example = "true")
        @NotNull Boolean notificationsEnabled

) {}
