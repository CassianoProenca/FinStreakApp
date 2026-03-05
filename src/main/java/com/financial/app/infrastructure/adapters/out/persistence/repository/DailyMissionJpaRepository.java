package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.DailyMissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DailyMissionJpaRepository extends JpaRepository<DailyMissionEntity, UUID> {
}
