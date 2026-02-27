package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadDailyMissionsPort;
import com.financial.app.application.ports.out.LoadUserMissionCompletedPort;
import com.financial.app.application.ports.out.SaveUserMissionCompletedPort;
import com.financial.app.domain.model.DailyMission;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.out.persistence.entity.DailyMissionEntity;
import com.financial.app.infrastructure.adapters.out.persistence.entity.UserMissionCompletedEntity;
import com.financial.app.infrastructure.adapters.out.persistence.repository.DailyMissionJpaRepository;
import com.financial.app.infrastructure.adapters.out.persistence.repository.TransactionJpaRepository;
import com.financial.app.infrastructure.adapters.out.persistence.repository.UserMissionCompletedJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaMissionAdapter implements LoadDailyMissionsPort, LoadUserMissionCompletedPort, SaveUserMissionCompletedPort {

    private final DailyMissionJpaRepository dailyMissionRepository;
    private final UserMissionCompletedJpaRepository userMissionCompletedRepository;
    private final TransactionJpaRepository transactionRepository;

    @Override
    public List<DailyMission> loadAll() {
        return dailyMissionRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public int countTodayTransactions(UUID userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return transactionRepository.countByUserIdAndDateBetween(userId, start, end);
    }

    @Override
    public int countTodayGoalDeposits(UUID userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return transactionRepository.countByUserIdAndTypeAndDateBetween(userId, TransactionType.GOAL_ALLOCATION, start, end);
    }

    @Override
    public boolean wasCompletedToday(UUID userId, UUID missionId, LocalDate date) {
        return userMissionCompletedRepository.existsByUserIdAndMissionIdAndCompletionDate(userId, missionId, date);
    }

    @SuppressWarnings("null")
    @Override
    public void save(UUID userId, UUID missionId, LocalDate date) {
        UserMissionCompletedEntity entity = UserMissionCompletedEntity.builder()
                .userId(userId)
                .missionId(missionId)
                .completionDate(date)
                .build();
        userMissionCompletedRepository.save(entity);
    }

    private DailyMission toDomain(DailyMissionEntity entity) {
        DailyMission mission = new DailyMission();
        mission.setId(entity.getId());
        mission.setTitle(entity.getTitle());
        mission.setDescription(entity.getDescription());
        mission.setXpReward(entity.getXpReward());
        mission.setMissionType(entity.getMissionType());
        mission.setRequiredCount(entity.getRequiredCount());
        mission.setCreatedAt(entity.getCreatedAt());
        mission.setUpdatedAt(entity.getUpdatedAt());
        mission.setVersion(entity.getVersion());
        return mission;
    }
}
