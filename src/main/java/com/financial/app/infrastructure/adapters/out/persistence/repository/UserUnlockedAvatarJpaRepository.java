package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.UserUnlockedAvatarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserUnlockedAvatarJpaRepository extends JpaRepository<UserUnlockedAvatarEntity, UUID> {
    List<UserUnlockedAvatarEntity> findByUserId(UUID userId);

    boolean existsByUserIdAndAvatarKey(UUID userId, String avatarKey);
}
