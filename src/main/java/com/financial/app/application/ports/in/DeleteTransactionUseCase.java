package com.financial.app.application.ports.in;

import java.util.UUID;

public interface DeleteTransactionUseCase {
    void execute(UUID userId, UUID transactionId);
}
