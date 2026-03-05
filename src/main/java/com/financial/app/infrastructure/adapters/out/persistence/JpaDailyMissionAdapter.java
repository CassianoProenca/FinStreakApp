package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadDailyMissionsPort;
import com.financial.app.application.ports.out.LoadUserMissionCompletedPort;
import com.financial.app.application.ports.out.SaveUserMissionCompletedPort;
import com.financial.app.domain.model.DailyMission;
import com.financial.app.infrastructure.adapters.out.persistence.entity.UserMissionCompletedEntity;
import com.financial.app.infrastructure.adapters.out.persistence.repository.DailyMissionJpaRepository;
import com.financial.app.infrastructure.adapters.out.persistence.repository.UserMissionCompletedJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaDailyMissionAdapter implements LoadDailyMissionsPort, LoadUserMissionCompletedPort, SaveUserMissionCompletedPort {

    private final DailyMissionJpaRepository missionRepository;
    private final UserMissionCompletedJpaRepository completedRepository;

    @Override
    public List<DailyMission> loadAll() {
        return missionRepository.findAll().stream()
                .map(e -> DailyMission.builder()
                        .id(e.getId())
                        .title(e.getTitle())
                        .description(e.getDescription())
                        .xpReward(e.getXpReward())
                        .missionType(e.getMissionType())
                        .requiredCount(e.getRequiredCount())
                        .build())
                .toList();
    }

    @Override
    public boolean wasCompletedToday(UUID userId, UUID missionId, LocalDate date) {
        return completedRepository.existsByUserIdAndMissionIdAndCompletionDate(userId, missionId, date);
    }

    @Override
    public void save(UUID userId, UUID missionId, LocalDate date) {
        UserMissionCompletedEntity entity = UserMissionCompletedEntity.builder()
                .userId(userId)
                .missionId(missionId)
                .completionDate(date)
                .build();
        completedRepository.save(entity);
    }
}
