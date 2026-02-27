package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.UserMissionCompletedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface UserMissionCompletedJpaRepository extends JpaRepository<UserMissionCompletedEntity, UUID> {
    boolean existsByUserIdAndMissionIdAndCompletionDate(UUID userId, UUID missionId, LocalDate completionDate);
}
