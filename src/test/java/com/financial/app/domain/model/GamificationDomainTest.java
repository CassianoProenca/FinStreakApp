package com.financial.app.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GamificationDomainTest {

    @Test
    @DisplayName("Should start streak when it is the first activity")
    void shouldStartStreakOnFirstActivity() {
        GamificationProfile profile = GamificationProfile.builder()
                .userId(UUID.randomUUID())
                .build();

        LocalDate activityDate = LocalDate.of(2023, 10, 1);
        boolean result = profile.checkStreak(activityDate);

        assertTrue(result);
        assertEquals(1, profile.getCurrentStreak());
        assertEquals(1, profile.getMaxStreak());
        assertEquals(activityDate, profile.getLastActivityDate());
    }

    @Test
    @DisplayName("Should increment streak on consecutive day activity")
    void shouldIncrementStreak() {
        LocalDate firstDate = LocalDate.of(2023, 10, 1);
        GamificationProfile profile = GamificationProfile.builder()
                .userId(UUID.randomUUID())
                .lastActivityDate(firstDate)
                .currentStreak(1)
                .maxStreak(1)
                .build();

        LocalDate nextDay = LocalDate.of(2023, 10, 2);
        boolean result = profile.checkStreak(nextDay);

        assertTrue(result);
        assertEquals(2, profile.getCurrentStreak());
        assertEquals(2, profile.getMaxStreak());
        assertEquals(nextDay, profile.getLastActivityDate());
    }

    @Test
    @DisplayName("Should not increment streak if activity is on the same day")
    void shouldNotIncrementOnSameDay() {
        LocalDate today = LocalDate.of(2023, 10, 1);
        GamificationProfile profile = GamificationProfile.builder()
                .userId(UUID.randomUUID())
                .lastActivityDate(today)
                .currentStreak(5)
                .maxStreak(10)
                .build();

        boolean result = profile.checkStreak(today);

        assertFalse(result); // same day → false (no new activity)
        assertEquals(5, profile.getCurrentStreak());
        assertEquals(10, profile.getMaxStreak());
        assertEquals(today, profile.getLastActivityDate());
    }

    @Test
    @DisplayName("Should reset streak if activity is not consecutive")
    void shouldResetStreakOnGap() {
        LocalDate lastDate = LocalDate.of(2023, 10, 1);
        GamificationProfile profile = GamificationProfile.builder()
                .userId(UUID.randomUUID())
                .lastActivityDate(lastDate)
                .currentStreak(10)
                .maxStreak(10)
                .build();

        // Skipped 10/02, activity on 10/03
        LocalDate gapDate = LocalDate.of(2023, 10, 3);
        boolean result = profile.checkStreak(gapDate);

        assertTrue(result); // new day → true
        assertEquals(1, profile.getCurrentStreak());
        assertEquals(10, profile.getMaxStreak()); // max preserves previous record
        assertEquals(gapDate, profile.getLastActivityDate());
    }

    @Test
    @DisplayName("xpWithinCurrentLevel should reflect XP inside current level window")
    void shouldCalculateXpWithinCurrentLevel() {
        GamificationProfile profile = GamificationProfile.builder()
                .userId(UUID.randomUUID())
                .totalXp(750L)
                .build();

        // level = (750/500)+1 = 2 → xpWithinLevel = 750 - (2-1)*500 = 250
        int level = profile.getLevel();
        long xpWithin = profile.getTotalXp() - ((long) (level - 1) * 500L);

        assertEquals(2, level);
        assertEquals(250L, xpWithin);
    }

    @Test
    @DisplayName("addXp should only increase totalXp for positive amounts")
    void shouldOnlyAddPositiveXp() {
        GamificationProfile profile = GamificationProfile.builder()
                .userId(UUID.randomUUID())
                .totalXp(100L)
                .build();

        profile.addXp(0);
        profile.addXp(-50);
        assertEquals(100L, profile.getTotalXp()); // unchanged

        profile.addXp(50);
        assertEquals(150L, profile.getTotalXp());
    }
}
