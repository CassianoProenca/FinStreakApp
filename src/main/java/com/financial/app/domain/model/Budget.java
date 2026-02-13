package com.financial.app.domain.model;

import com.financial.app.domain.model.enums.TransactionCategory;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Budget extends BaseDomainEntity {
    private UUID userId;
    private TransactionCategory category;
    private BigDecimal limitAmount;
    private int month;
    private int year;
}
