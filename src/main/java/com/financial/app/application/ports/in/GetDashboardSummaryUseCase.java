package com.financial.app.application.ports.in;

import com.financial.app.infrastructure.adapters.in.web.dto.response.DashboardSummaryResponse;
import java.util.UUID;

public interface GetDashboardSummaryUseCase {
    DashboardSummaryResponse execute(UUID userId, int month, int year);
}
