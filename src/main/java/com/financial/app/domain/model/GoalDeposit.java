package com.financial.app.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDeposit extends BaseDomainEntity {
    private UUID goalId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;
}
