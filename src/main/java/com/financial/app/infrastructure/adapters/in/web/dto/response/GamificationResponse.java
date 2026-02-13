package com.financial.app.infrastructure.adapters.in.web.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record GamificationResponse(
        UUID userId,
        String name,
        String avatarUrl,
        Integer currentStreak,
        Integer maxStreak,
        Long totalXp,
        Integer level,
        Long xpForNextLevel,
        LocalDate lastActivityDate
) {}
