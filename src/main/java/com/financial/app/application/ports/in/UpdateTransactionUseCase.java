package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.domain.model.Transaction;
import java.util.UUID;

public interface UpdateTransactionUseCase {
    Transaction execute(UUID userId, UUID transactionId, CreateTransactionCommand command);
}
