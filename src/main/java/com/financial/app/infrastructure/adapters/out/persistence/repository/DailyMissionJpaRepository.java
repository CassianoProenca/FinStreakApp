package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.DailyMissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DailyMissionJpaRepository extends JpaRepository<DailyMissionEntity, UUID> {
}
