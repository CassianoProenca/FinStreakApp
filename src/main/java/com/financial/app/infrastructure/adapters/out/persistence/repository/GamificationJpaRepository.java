package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.GamificationProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GamificationJpaRepository extends JpaRepository<GamificationProfileEntity, UUID> {
    Optional<GamificationProfileEntity> findByUserId(UUID userId);
}
