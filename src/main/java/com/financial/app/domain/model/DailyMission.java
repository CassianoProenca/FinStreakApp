package com.financial.app.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMission extends BaseDomainEntity {

    private String title;
    private String description;
    private int xpReward;
    private String missionType;
    private int requiredCount;
}
