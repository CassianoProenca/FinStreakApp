package com.financial.app.application.ports.out;

import com.financial.app.domain.model.DailyMission;

import java.util.List;

public interface LoadDailyMissionsPort {
    List<DailyMission> loadAll();
}
