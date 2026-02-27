package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadUnlockedAvatarsPort;
import com.financial.app.application.ports.out.SaveUnlockedAvatarPort;
import com.financial.app.infrastructure.adapters.out.persistence.entity.UserUnlockedAvatarEntity;
import com.financial.app.infrastructure.adapters.out.persistence.repository.UserUnlockedAvatarJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaUnlockedAvatarAdapter implements LoadUnlockedAvatarsPort, SaveUnlockedAvatarPort {

    private final UserUnlockedAvatarJpaRepository repository;

    @Override
    public List<String> loadAvatarKeys(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(UserUnlockedAvatarEntity::getAvatarKey)
                .toList();
    }

    @Override
    public void save(UUID userId, String avatarKey) {
        UserUnlockedAvatarEntity entity = UserUnlockedAvatarEntity.builder()
                .userId(userId)
                .avatarKey(avatarKey)
                .build();
        repository.save(entity);
    }
}
