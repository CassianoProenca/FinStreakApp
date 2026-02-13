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

        // 1. Update Streak
        profile.checkStreak(LocalDate.now());

        // 2. Add XP for activity (e.g., 50 XP per transaction/check-in)
        profile.addXp(50);

        // 3. Check for Achievements
        checkAndAwardAchievements(profile);

        // 4. Notifications
        if (profile.getCurrentStreak() > oldStreak) {
            notificationPort.notifyUser(userId, "🔥 Streak increased to " + profile.getCurrentStreak() + "!");
        }
        
        if (profile.getLevel() > oldLevel) {
            notificationPort.notifyUser(userId, "🆙 Level Up! You are now Level " + profile.getLevel() + "!");
        }

        saveProfilePort.save(profile);
    }

    private GamificationProfile createInitialProfile(UUID userId) {
        return GamificationProfile.builder()
                .userId(userId)
                .currentStreak(0)
                .maxStreak(0)
                .totalXp(0L)
                .build();
    }

    private void checkAndAwardAchievements(GamificationProfile profile) {
        UUID userId = profile.getUserId();

        // Medalha: Primeiros Passos
        if (!loadAchievementsPort.hasAchievement(userId, AchievementType.FIRST_STEPS)) {
            awardAchievement(userId, AchievementType.FIRST_STEPS, "Primeiros Passos", "Você registrou sua primeira atividade financeira!");
            profile.addXp(200); // Bônus por medalha
        }

        // Medalha: Streak de 7 dias
        if (profile.getCurrentStreak() >= 7 && !loadAchievementsPort.hasAchievement(userId, AchievementType.STREAK_7)) {
            awardAchievement(userId, AchievementType.STREAK_7, "Uma Semana de Foco", "7 dias seguidos de controle financeiro!");
            profile.addXp(500);
        }

        // Medalha: Streak de 30 dias
        if (profile.getCurrentStreak() >= 30 && !loadAchievementsPort.hasAchievement(userId, AchievementType.STREAK_30)) {
            awardAchievement(userId, AchievementType.STREAK_30, "Mestre da Constância", "30 dias de ofensiva financeira!");
            profile.addXp(1500);
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
        notificationPort.notifyUser(userId, "🏆 New Badge Earned: " + name + "!");
    }
}
