package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Achievement;

public interface SaveAchievementPort {
    Achievement save(Achievement achievement);
}
