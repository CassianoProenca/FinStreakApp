package com.financial.app.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "daily_missions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMissionEntity extends AbstractBaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward;

    @Column(name = "mission_type", nullable = false, length = 50)
    private String missionType;

    @Column(name = "required_count", nullable = false)
    private int requiredCount;
}
