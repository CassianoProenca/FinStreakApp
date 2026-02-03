package com.financial.app.application.ports.out;

import com.financial.app.domain.model.GamificationProfile;
import java.util.Optional;
import java.util.UUID;

public interface LoadGamificationProfilePort {
    Optional<GamificationProfile> loadByUserId(UUID userId);
}
