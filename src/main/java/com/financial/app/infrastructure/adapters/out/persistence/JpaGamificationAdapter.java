package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.application.ports.out.SaveGamificationProfilePort;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.infrastructure.adapters.out.persistence.entity.GamificationProfileEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.GamificationMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.GamificationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaGamificationAdapter implements LoadGamificationProfilePort, SaveGamificationProfilePort {

    private final GamificationJpaRepository repository;

    public JpaGamificationAdapter(GamificationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<GamificationProfile> loadByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(GamificationMapper::toDomain);
    }

    @Override
    public GamificationProfile save(GamificationProfile profile) {
        // If profile has an ID, load and update the existing entity
        if (profile.getId() != null) {
            Optional<GamificationProfileEntity> existingEntityOpt = repository.findById(profile.getId());
            if (existingEntityOpt.isPresent()) {
                GamificationProfileEntity existingEntity = existingEntityOpt.get();

                // Update fields
                existingEntity.setUserId(profile.getUserId());
                existingEntity.setCurrentStreak(profile.getCurrentStreak());
                existingEntity.setMaxStreak(profile.getMaxStreak());
                existingEntity.setTotalXp(profile.getTotalXp());
                existingEntity.setLastActivityDate(profile.getLastActivityDate());

                GamificationProfileEntity saved = repository.save(existingEntity);
                return GamificationMapper.toDomain(saved);
            }
        }

        // For new profiles, create a new entity
        GamificationProfileEntity entity = GamificationMapper.toEntity(profile);
        GamificationProfileEntity saved = repository.save(entity);
        return GamificationMapper.toDomain(saved);
    }
}
