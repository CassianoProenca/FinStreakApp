package com.financial.app.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DailyMission {

    private UUID id;
    private String title;
    private String description;
    private int xpReward;
    private String missionType; // TRANSACTION_COUNT, GOAL_DEPOSIT
    private int requiredCount;
}
