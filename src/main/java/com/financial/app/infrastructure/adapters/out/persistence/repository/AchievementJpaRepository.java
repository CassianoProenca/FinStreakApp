package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.domain.model.enums.AchievementType;
import com.financial.app.infrastructure.adapters.out.persistence.entity.AchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AchievementJpaRepository extends JpaRepository<AchievementEntity, UUID> {
    List<AchievementEntity> findByUserId(UUID userId);
    boolean existsByUserIdAndType(UUID userId, AchievementType type);
}
