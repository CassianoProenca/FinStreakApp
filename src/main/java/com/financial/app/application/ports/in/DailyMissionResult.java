package com.financial.app.application.ports.in;

import java.util.UUID;

public record DailyMissionResult(
        UUID id,
        String title,
        String description,
        int xpReward,
        int currentCount,
        int requiredCount,
        boolean completed
) {}
