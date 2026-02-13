package com.financial.app.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GamificationProfile extends BaseDomainEntity {

    private UUID userId;

    @Builder.Default
    private Integer currentStreak = 0;

    @Builder.Default
    private Integer maxStreak = 0;

    @Builder.Default
    private Long totalXp = 0L;

    private LocalDate lastActivityDate;

    public int getLevel() {
        // Lógica: Nível 1 a cada 500 XP conforme design
        return (int) (totalXp / 500) + 1;
    }

    public long getXpForNextLevel() {
        return getLevel() * 500L;
    }

    public void addXp(long amount) {
        if (amount > 0) {
            this.totalXp += amount;
        }
    }

    public void checkStreak(LocalDate activityDate) {
        if (lastActivityDate == null) {
            currentStreak = 1;
        } else if (activityDate.equals(lastActivityDate)) {
            // Already active today, do nothing
            return;
        } else if (activityDate.equals(lastActivityDate.plusDays(1))) {
            // Consecutive day
            currentStreak++;
        } else {
            // Broken streak
            currentStreak = 1;
        }

        if (currentStreak > maxStreak) {
            maxStreak = currentStreak;
        }
        lastActivityDate = activityDate;
    }
}
