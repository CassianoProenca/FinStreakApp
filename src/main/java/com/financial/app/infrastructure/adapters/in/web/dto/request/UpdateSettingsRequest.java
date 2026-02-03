package com.financial.app.infrastructure.adapters.in.web.dto.request;

import com.financial.app.domain.model.enums.Theme;
import jakarta.validation.constraints.NotNull;

public record UpdateSettingsRequest(
        @NotNull
        Theme theme,
        
        @NotNull
        Boolean notificationsEnabled
) {}
