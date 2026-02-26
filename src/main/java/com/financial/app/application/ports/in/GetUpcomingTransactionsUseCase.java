package com.financial.app.application.ports.in;

import com.financial.app.infrastructure.adapters.in.web.dto.response.UpcomingTransactionResponse;

import java.util.List;
import java.util.UUID;

public interface GetUpcomingTransactionsUseCase {
    List<UpcomingTransactionResponse> execute(UUID userId);
}
