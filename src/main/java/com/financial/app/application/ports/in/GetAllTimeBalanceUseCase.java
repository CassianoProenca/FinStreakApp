package com.financial.app.application.ports.in;

import com.financial.app.infrastructure.adapters.in.web.dto.response.BalanceResponse;

import java.util.UUID;

public interface GetAllTimeBalanceUseCase {
    BalanceResponse execute(UUID userId);
}
