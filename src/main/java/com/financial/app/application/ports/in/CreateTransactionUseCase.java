package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.domain.model.Transaction;

public interface CreateTransactionUseCase {
    Transaction execute(CreateTransactionCommand command);
}
