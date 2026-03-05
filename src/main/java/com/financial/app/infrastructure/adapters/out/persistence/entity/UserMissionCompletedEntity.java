package com.financial.app.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "user_missions_completed",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "mission_id", "completion_date"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionCompletedEntity extends AbstractBaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "mission_id", nullable = false)
    private UUID missionId;

    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;
}
