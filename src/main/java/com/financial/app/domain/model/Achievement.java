package com.financial.app.domain.model;

import com.financial.app.domain.model.enums.AchievementType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Achievement extends BaseDomainEntity {
    private UUID userId;
    private AchievementType type;
    private String name;
    private String description;
    private LocalDateTime earnedAt;
}
