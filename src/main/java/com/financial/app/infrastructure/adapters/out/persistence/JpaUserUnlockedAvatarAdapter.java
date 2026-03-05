package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadUnlockedAvatarsPort;
import com.financial.app.infrastructure.adapters.out.persistence.repository.UserUnlockedAvatarJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaUserUnlockedAvatarAdapter implements LoadUnlockedAvatarsPort {

    private final UserUnlockedAvatarJpaRepository repository;

    @Override
    public List<String> loadAvatarKeys(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(e -> e.getAvatarKey())
                .toList();
    }
}
