package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.GetGamificationProfileUseCase;
import com.financial.app.application.ports.out.LoadGamificationProfilePort;
import com.financial.app.domain.model.GamificationProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetGamificationProfileService implements GetGamificationProfileUseCase {

    private final LoadGamificationProfilePort loadGamificationProfilePort;

    public GetGamificationProfileService(LoadGamificationProfilePort loadGamificationProfilePort) {
        this.loadGamificationProfilePort = loadGamificationProfilePort;
    }

    @Override
    public GamificationProfile execute(UUID userId) {
        return loadGamificationProfilePort.loadByUserId(userId)
                .orElseGet(() -> GamificationProfile.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .maxStreak(0)
                        .totalXp(0L)
                        .build());
    }
}
