package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
