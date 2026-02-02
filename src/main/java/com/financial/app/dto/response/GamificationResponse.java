package com.financial.app.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record GamificationResponse(
        UUID userId,
        Integer currentStreak,
        Integer maxStreak,
        Long totalXp,
        LocalDate lastActivityDate
) {}