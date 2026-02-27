package com.financial.app.application.ports.out;

import com.financial.app.domain.model.DailyMission;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoadDailyMissionsPort {
    List<DailyMission> loadAll();
    int countTodayTransactions(UUID userId, LocalDate date);
    int countTodayGoalDeposits(UUID userId, LocalDate date);
}
