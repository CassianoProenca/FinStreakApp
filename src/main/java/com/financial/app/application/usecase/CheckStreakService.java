package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveAchievementPort;
import com.financial.app.application.ports.out.SaveGamificationProfilePort;
import com.financial.app.domain.model.Achievement;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.domain.model.enums.AchievementType;
import com.financial.app.domain.model.enums.NotificationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class CheckStreakService implements CheckStreakUseCase {

    private final LoadGamificationProfilePort loadProfilePort;
    private final SaveGamificationProfilePort saveProfilePort;
    private final LoadAchievementsPort loadAchievementsPort;
    private final SaveAchievementPort saveAchievementPort;
    private final NotificationPort notificationPort;

    public CheckStreakService(LoadGamificationProfilePort loadProfilePort,
                              SaveGamificationProfilePort saveProfilePort,
                              LoadAchievementsPort loadAchievementsPort,
                              SaveAchievementPort saveAchievementPort,
                              NotificationPort notificationPort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
        this.loadAchievementsPort = loadAchievementsPort;
        this.saveAchievementPort = saveAchievementPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void execute(UUID userId) {
        GamificationProfile profile = loadProfilePort.loadByUserId(userId)
                .orElseGet(() -> createInitialProfile(userId));

        int oldStreak = profile.getCurrentStreak();
        int oldLevel = profile.getLevel();

        // 1. Update Streak — returns true only if this is a new day (first activity today)
        boolean isNewActivity = profile.checkStreak(LocalDate.now());

        // 2. Add XP for activity only once per day
        if (isNewActivity) {
            profile.addXp(50);
        }

        // 3. Check for Achievements
        checkAndAwardAchievements(profile);

        // 4. Notifications
        if (profile.getCurrentStreak() > oldStreak) {
            notificationPort.notifyUser(userId, "🔥 Streak aumentou para " + profile.getCurrentStreak() + " dias!", NotificationType.STREAK);
        }

        if (profile.getLevel() > oldLevel) {
            notificationPort.notifyUser(userId, "🆙 Subiu de Nível! Você está no Nível " + profile.getLevel() + "!", NotificationType.LEVEL_UP);
        }

        saveProfilePort.save(profile);
    }

    private GamificationProfile createInitialProfile(UUID userId) {
        // Build but do NOT save yet — it will be saved at the end of execute() so it gets a real ID (#19)
        return GamificationProfile.builder()
                .userId(userId)
                .currentStreak(0)
                .maxStreak(0)
                .totalXp(0L)
                .build();
    }

    private void checkAndAwardAchievements(GamificationProfile profile) {
        UUID userId = profile.getUserId();

        // Medalha: Primeiros Passos (#17 — XP granted)
        if (!loadAchievementsPort.hasAchievement(userId, AchievementType.FIRST_STEPS)) {
            awardAchievement(userId, AchievementType.FIRST_STEPS, "Primeiros Passos", "Você registrou sua primeira atividade financeira!");
            profile.addXp(200);
        }

        // Medalha: Streak de 7 dias (#17 — XP granted)
        if (profile.getCurrentStreak() >= 7 && !loadAchievementsPort.hasAchievement(userId, AchievementType.STREAK_7)) {
            awardAchievement(userId, AchievementType.STREAK_7, "Uma Semana de Foco", "7 dias seguidos de controle financeiro!");
            profile.addXp(500);
        }

        // Medalha: Streak de 30 dias (#17 — XP granted)
        if (profile.getCurrentStreak() >= 30 && !loadAchievementsPort.hasAchievement(userId, AchievementType.STREAK_30)) {
            awardAchievement(userId, AchievementType.STREAK_30, "Mestre da Constância", "30 dias de ofensiva financeira!");
            profile.addXp(1500);
        }

        // Medalha: ELITE_SAVER — concedida ao atingir nível 10 (#18)
        if (profile.getLevel() >= 10 && !loadAchievementsPort.hasAchievement(userId, AchievementType.ELITE_SAVER)) {
            awardAchievement(userId, AchievementType.ELITE_SAVER, "Poupador de Elite", "Você atingiu o nível 10!");
            profile.addXp(2000);
        }
    }

    private void awardAchievement(UUID userId, AchievementType type, String name, String description) {
        Achievement achievement = Achievement.builder()
                .userId(userId)
                .type(type)
                .name(name)
                .description(description)
                .earnedAt(LocalDateTime.now())
                .build();
        saveAchievementPort.save(achievement);
        notificationPort.notifyUser(userId, "🏆 Nova Medalha: " + name + "!", NotificationType.ACHIEVEMENT);
    }
}
