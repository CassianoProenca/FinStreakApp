package com.financial.app.application.ports.out;

import java.time.LocalDate;
import java.util.UUID;

public interface SaveUserMissionCompletedPort {
    void save(UUID userId, UUID missionId, LocalDate date);
}
