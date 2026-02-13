package com.financial.app.infrastructure.adapters.out.persistence.entity;

import com.financial.app.domain.model.enums.TransactionCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fin_budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "category", "month", "year"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetEntity extends AbstractBaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory category;

    @Column(name = "limit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal limitAmount;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;
}
