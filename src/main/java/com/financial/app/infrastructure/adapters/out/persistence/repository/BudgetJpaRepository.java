package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.infrastructure.adapters.out.persistence.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetJpaRepository extends JpaRepository<BudgetEntity, UUID> {
    Optional<BudgetEntity> findByUserIdAndCategoryAndMonthAndYear(UUID userId, TransactionCategory category, int month, int year);
    List<BudgetEntity> findByUserIdAndMonthAndYear(UUID userId, int month, int year);
}
