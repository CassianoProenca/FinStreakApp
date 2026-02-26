package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.GoalHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;
import java.util.UUID;

public interface GoalHistoryJpaRepository extends JpaRepository<GoalHistoryEntity, UUID> {
    List<GoalHistoryEntity> findByGoalIdOrderByTransactionDateDesc(UUID goalId);

    @Modifying
    void deleteByGoalId(UUID goalId);
}
