package com.financial.app.application.ports.in;

import com.financial.app.domain.model.GamificationProfile;
import java.util.UUID;

public interface GetGamificationProfileUseCase {
    GamificationProfile execute(UUID userId);
}
