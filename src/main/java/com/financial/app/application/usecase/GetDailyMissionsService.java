package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.DailyMissionResult;
import com.financial.app.application.ports.in.GetDailyMissionsUseCase;
import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.out.LoadDailyMissionsPort;
import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.application.ports.out.LoadUserMissionCompletedPort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveGamificationProfilePort;
import com.financial.app.application.ports.out.SaveUserMissionCompletedPort;
import com.financial.app.domain.model.DailyMission;
import com.financial.app.domain.model.enums.NotificationType;
import com.financial.app.domain.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final LoadTransactionPort loadTransactionPort;
    private final LoadGamificationProfilePort loadGamificationProfilePort;
    private final SaveGamificationProfilePort saveGamificationProfilePort;
    private final NotificationPort notificationPort;

    @Override
    public List<DailyMissionResult> execute(UUID userId) {
        List<DailyMission> missions = loadDailyMissionsPort.loadAll();
        LocalDate today = LocalDate.now();

        List<DailyMissionResult> results = new ArrayList<>();
        for (DailyMission mission : missions) {
            boolean alreadyCompleted = loadUserMissionCompletedPort.wasCompletedToday(userId, mission.getId(), today);
            int currentCount = countProgress(userId, mission.getMissionType(), today);

            if (!alreadyCompleted && currentCount >= mission.getRequiredCount()) {
                try {
                    saveUserMissionCompletedPort.save(userId, mission.getId(), today);
                    notificationPort.notifyUser(
                            userId,
                            "Missão concluída: " + mission.getTitle() + "! +" + mission.getXpReward() + " XP",
                            NotificationType.SYSTEM
                    );
                    awardXp(userId, mission.getXpReward());
                    alreadyCompleted = true;
                } catch (Exception e) {
                    log.warn("Não foi possível completar missão {} para usuário {}: {}", mission.getId(), userId, e.getMessage());
                }
            }

            results.add(new DailyMissionResult(
                    mission.getId(),
                    mission.getTitle(),
                    mission.getDescription(),
                    mission.getXpReward(),
                    Math.min(currentCount, mission.getRequiredCount()),
                    mission.getRequiredCount(),
                    alreadyCompleted
            ));
        }
        return results;
    }

    private int countProgress(UUID userId, String missionType, LocalDate today) {
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        return switch (missionType) {
            case "TRANSACTION_COUNT" -> {
                TransactionQuery query = new TransactionQuery(userId, start, end, TransactionType.EXPENSE, null, 0, Integer.MAX_VALUE);
                yield loadTransactionPort.loadAllByQuery(query).size();
            }
            case "GOAL_DEPOSIT" -> {
                TransactionQuery query = new TransactionQuery(userId, start, end, TransactionType.GOAL_ALLOCATION, null, 0, Integer.MAX_VALUE);
                yield loadTransactionPort.loadAllByQuery(query).size();
            }
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
