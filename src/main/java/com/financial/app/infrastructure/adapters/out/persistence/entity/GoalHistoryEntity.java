package com.financial.app.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fin_goal_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalHistoryEntity extends AbstractBaseEntity {

    @Column(name = "goal_id", nullable = false)
    private UUID goalId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
}
