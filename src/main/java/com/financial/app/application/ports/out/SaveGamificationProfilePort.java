package com.financial.app.application.ports.out;

import com.financial.app.domain.model.GamificationProfile;

public interface SaveGamificationProfilePort {
    GamificationProfile save(GamificationProfile profile);
}
