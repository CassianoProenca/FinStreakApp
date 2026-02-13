package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Achievement;
import com.financial.app.domain.model.enums.AchievementType;
import java.util.List;
import java.util.UUID;

public interface LoadAchievementsPort {
    List<Achievement> loadByUserId(UUID userId);
    boolean hasAchievement(UUID userId, AchievementType type);
}
