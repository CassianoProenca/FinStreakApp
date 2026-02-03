package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveGamificationProfilePort;
import com.financial.app.domain.model.GamificationProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class CheckStreakService implements CheckStreakUseCase {

    private final LoadGamificationProfilePort loadPort;
    private final SaveGamificationProfilePort savePort;
    private final NotificationPort notificationPort;

    public CheckStreakService(LoadGamificationProfilePort loadPort, 
                              SaveGamificationProfilePort savePort,
                              NotificationPort notificationPort) {
        this.loadPort = loadPort;
        this.savePort = savePort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void execute(UUID userId) {
        GamificationProfile profile = loadPort.loadByUserId(userId)
                .orElseGet(() -> GamificationProfile.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .maxStreak(0)
                        .totalXp(0L)
                        .build());
        
        profile.initialize();

        int oldStreak = profile.getCurrentStreak();
        
        profile.checkStreak(LocalDate.now());

        if (profile.getCurrentStreak() > oldStreak) {
             notificationPort.notifyUser(userId, "🔥 Streak increased to " + profile.getCurrentStreak() + "!");
        }

        savePort.save(profile);
    }
}
