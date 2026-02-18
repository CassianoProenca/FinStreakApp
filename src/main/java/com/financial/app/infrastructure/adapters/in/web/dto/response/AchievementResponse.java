package com.financial.app.infrastructure.adapters.in.web.dto.response;

import com.financial.app.domain.model.enums.AchievementType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AchievementResponse(
        UUID id,
        AchievementType type,
        String name,
        String description,
        LocalDateTime earnedAt
) {}
