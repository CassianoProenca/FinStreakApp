package com.financial.app.application.ports.in;

import java.util.List;
import java.util.UUID;

public interface GetDailyMissionsUseCase {
    List<DailyMissionResult> execute(UUID userId);
}
