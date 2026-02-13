package com.financial.app.infrastructure.adapters.out.persistence.entity;

import com.financial.app.domain.model.enums.GoalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fin_goals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalEntity extends AbstractBaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    private BigDecimal currentAmount;

    @Column(nullable = false)
    private java.time.LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status;

    @Column(name = "icon_key")
    private String iconKey;
}
