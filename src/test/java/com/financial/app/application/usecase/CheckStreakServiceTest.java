package com.financial.app.application.usecase;

import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.application.ports.out.NotificationPort;
import com.financial.app.application.ports.out.SaveAchievementPort;
import com.financial.app.application.ports.out.SaveGamificationProfilePort;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.domain.model.enums.AchievementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckStreakServiceTest {

    @Mock private LoadGamificationProfilePort loadProfilePort;
    @Mock private SaveGamificationProfilePort saveProfilePort;
    @Mock private LoadAchievementsPort loadAchievementsPort;
    @Mock private SaveAchievementPort saveAchievementPort;
    @Mock private NotificationPort notificationPort;

    @InjectMocks
    private CheckStreakService service;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        // FIRST_STEPS already earned by default — keeps scenarios focused on XP
        when(loadAchievementsPort.hasAchievement(any(), any())).thenReturn(true);
        when(saveProfilePort.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("Should add 50 XP on first activity of the day")
    void shouldAddXpOnFirstActivityOfDay() {
        GamificationProfile profile = GamificationProfile.builder()
                .userId(userId)
                .totalXp(0L)
                .currentStreak(0)
                .maxStreak(0)
                .lastActivityDate(LocalDate.now().minusDays(1)) // yesterday
                .build();

        when(loadProfilePort.loadByUserId(userId)).thenReturn(Optional.of(profile));

        service.execute(userId);

        ArgumentCaptor<GamificationProfile> captor = ArgumentCaptor.forClass(GamificationProfile.class);
        verify(saveProfilePort).save(captor.capture());
        assertEquals(50L, captor.getValue().getTotalXp());
    }

    @Test
    @DisplayName("Should NOT add XP when called a second time on the same day")
    void shouldNotAddXpTwiceOnSameDay() {
        GamificationProfile profile = GamificationProfile.builder()
                .userId(userId)
                .totalXp(50L)
                .currentStreak(1)
                .maxStreak(1)
                .lastActivityDate(LocalDate.now()) // already active today
                .build();

        when(loadProfilePort.loadByUserId(userId)).thenReturn(Optional.of(profile));

        service.execute(userId);

        ArgumentCaptor<GamificationProfile> captor = ArgumentCaptor.forClass(GamificationProfile.class);
        verify(saveProfilePort).save(captor.capture());
        assertEquals(50L, captor.getValue().getTotalXp()); // unchanged
    }

    @Test
    @DisplayName("Should create initial profile and add XP when user has no profile")
    void shouldCreateProfileAndAddXpForNewUser() {
        when(loadProfilePort.loadByUserId(userId)).thenReturn(Optional.empty());
        // FIRST_STEPS not yet earned → will award it (+200 XP). streak < 7 so STREAK_7/30 not checked.
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.FIRST_STEPS)).thenReturn(false);

        service.execute(userId);

        ArgumentCaptor<GamificationProfile> captor = ArgumentCaptor.forClass(GamificationProfile.class);
        verify(saveProfilePort).save(captor.capture());
        // 50 (daily) + 200 (FIRST_STEPS bonus)
        assertEquals(250L, captor.getValue().getTotalXp());
        assertEquals(1, captor.getValue().getCurrentStreak());
    }

    @Test
    @DisplayName("Should award STREAK_7 achievement and bonus XP at 7-day streak")
    void shouldAwardStreak7Achievement() {
        GamificationProfile profile = GamificationProfile.builder()
                .userId(userId)
                .totalXp(300L)
                .currentStreak(6)
                .maxStreak(6)
                .lastActivityDate(LocalDate.now().minusDays(1))
                .build();

        when(loadProfilePort.loadByUserId(userId)).thenReturn(Optional.of(profile));
        // streak will reach 7 → STREAK_7 not yet earned; FIRST_STEPS already earned (setUp default)
        when(loadAchievementsPort.hasAchievement(userId, AchievementType.STREAK_7)).thenReturn(false);

        service.execute(userId);

        verify(saveAchievementPort).save(argThat(a -> a.getType() == AchievementType.STREAK_7));
        ArgumentCaptor<GamificationProfile> captor = ArgumentCaptor.forClass(GamificationProfile.class);
        verify(saveProfilePort).save(captor.capture());
        // 300 + 50 (daily) + 500 (STREAK_7 bonus)
        assertEquals(850L, captor.getValue().getTotalXp());
        assertEquals(7, captor.getValue().getCurrentStreak());
    }

    @Test
    @DisplayName("Should NOT award STREAK_7 if already earned")
    void shouldNotAwardStreak7IfAlreadyEarned() {
        GamificationProfile profile = GamificationProfile.builder()
                .userId(userId)
                .totalXp(800L)
                .currentStreak(6)
                .maxStreak(6)
                .lastActivityDate(LocalDate.now().minusDays(1))
                .build();

        when(loadProfilePort.loadByUserId(userId)).thenReturn(Optional.of(profile));
        // STREAK_7 already earned
        when(loadAchievementsPort.hasAchievement(any(), any())).thenReturn(true);

        service.execute(userId);

        verify(saveAchievementPort, never()).save(any());
        ArgumentCaptor<GamificationProfile> captor = ArgumentCaptor.forClass(GamificationProfile.class);
        verify(saveProfilePort).save(captor.capture());
        assertEquals(850L, captor.getValue().getTotalXp()); // only 50 XP added
    }
}
