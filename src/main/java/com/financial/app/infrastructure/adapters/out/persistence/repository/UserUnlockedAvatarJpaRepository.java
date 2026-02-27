package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.UserUnlockedAvatarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserUnlockedAvatarJpaRepository extends JpaRepository<UserUnlockedAvatarEntity, UUID> {
    List<UserUnlockedAvatarEntity> findByUserId(UUID userId);
}
