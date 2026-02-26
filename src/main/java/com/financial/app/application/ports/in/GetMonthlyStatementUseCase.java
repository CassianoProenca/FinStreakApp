package com.financial.app.application.ports.in;

import com.financial.app.infrastructure.adapters.in.web.dto.response.MonthlyStatementResponse;

import java.util.UUID;

public interface GetMonthlyStatementUseCase {
    MonthlyStatementResponse execute(UUID userId, int month, int year);
}
