package com.financial.app.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GamificationDomainTest {

    @Test
    @DisplayName("Should start streak when it is the first activity")
    void shouldStartStreakOnFirstActivity() {
        GamificationProfile profile = GamificationProfile.builder()
                .userId(UUID.randomUUID())
                .build();

        LocalDate activityDate = LocalDate.of(2023, 10, 1);
        profile.checkStreak(activityDate);

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
        profile.checkStreak(nextDay);

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

        profile.checkStreak(today);

        assertEquals(5, profile.getCurrentStreak());
        assertEquals(10, profile.getMaxStreak()); // Max shouldn't change
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
        profile.checkStreak(gapDate);

        assertEquals(1, profile.getCurrentStreak());
        assertEquals(10, profile.getMaxStreak()); // Max stored previous record
        assertEquals(gapDate, profile.getLastActivityDate());
    }
}
