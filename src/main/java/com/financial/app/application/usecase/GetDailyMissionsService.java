package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.DailyMissionResult;
import com.financial.app.application.ports.in.GetDailyMissionsUseCase;
import com.financial.app.application.ports.out.LoadDailyMissionsPort;
import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.application.ports.out.LoadUserMissionCompletedPort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveGamificationProfilePort;
import com.financial.app.application.ports.out.SaveUserMissionCompletedPort;
import com.financial.app.domain.model.DailyMission;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.domain.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GetDailyMissionsService implements GetDailyMissionsUseCase {

    private final LoadDailyMissionsPort loadDailyMissionsPort;
    private final LoadUserMissionCompletedPort loadUserMissionCompletedPort;
    private final SaveUserMissionCompletedPort saveUserMissionCompletedPort;
    private final NotificationPort notificationPort;
    private final LoadGamificationProfilePort loadGamificationProfilePort;
    private final SaveGamificationProfilePort saveGamificationProfilePort;

    @Override
    public List<DailyMissionResult> execute(UUID userId) {
        List<DailyMission> missions = loadDailyMissionsPort.loadAll();
        LocalDate today = LocalDate.now();

        List<DailyMissionResult> results = new ArrayList<>();
        for (DailyMission mission : missions) {
            boolean completed = loadUserMissionCompletedPort.wasCompletedToday(userId, mission.getId(), today);

            int currentCount = getCurrentCount(userId, mission.getMissionType(), today);

            if (!completed && currentCount >= mission.getRequiredCount()) {
                try {
                    saveUserMissionCompletedPort.save(userId, mission.getId(), today);
                    notificationPort.notifyUser(userId,
                            "✅ Missão concluída: " + mission.getTitle() + "!",
                            NotificationType.SYSTEM);
                    awardXp(userId, mission.getXpReward());
                    completed = true;
                } catch (Exception e) {
                    log.warn("Could not complete mission {} for user {}: {}", mission.getId(), userId, e.getMessage());
                }
            }

            results.add(new DailyMissionResult(
                    mission.getId(),
                    mission.getTitle(),
                    mission.getDescription(),
                    mission.getXpReward(),
                    Math.min(currentCount, mission.getRequiredCount()),
                    mission.getRequiredCount(),
                    completed
            ));
        }
        return results;
    }

    private int getCurrentCount(UUID userId, String missionType, LocalDate today) {
        return switch (missionType) {
            case "TRANSACTION_COUNT" -> loadDailyMissionsPort.countTodayTransactions(userId, today);
            case "GOAL_DEPOSIT" -> loadDailyMissionsPort.countTodayGoalDeposits(userId, today);
            default -> 0;
        };
    }

    private void awardXp(UUID userId, int xpReward) {
        loadGamificationProfilePort.loadByUserId(userId).ifPresent(profile -> {
            profile.addXp(xpReward);
            saveGamificationProfilePort.save(profile);
        });
    }
}
