package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.SaveAchievementPort;
import com.financial.app.domain.model.Achievement;
import com.financial.app.domain.model.enums.AchievementType;
import com.financial.app.infrastructure.adapters.out.persistence.entity.AchievementEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.AchievementMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.AchievementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaAchievementAdapter implements SaveAchievementPort, LoadAchievementsPort {

    private final AchievementJpaRepository repository;

    @Override
    public Achievement save(Achievement achievement) {
        AchievementEntity entity = AchievementMapper.toEntity(achievement);
        return AchievementMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Achievement> loadByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(AchievementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAchievement(UUID userId, AchievementType type) {
        return repository.existsByUserIdAndType(userId, type);
    }
}
