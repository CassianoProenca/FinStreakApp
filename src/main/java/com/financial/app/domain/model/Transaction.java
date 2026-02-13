package com.financial.app.domain.model;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
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
public class Transaction extends BaseDomainEntity {

    private UUID userId;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private TransactionCategory category;
    private LocalDateTime date;

    @Builder.Default
    private boolean isRecurring = false;
    private String frequency; // e.g., MONTHLY, WEEKLY
    private Integer repeatDay;
    private String iconKey;
    private UUID parentTransactionId;
}
