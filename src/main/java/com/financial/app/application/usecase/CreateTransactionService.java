package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.in.CreateTransactionUseCase;
import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateTransactionService implements CreateTransactionUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveTransactionPort saveTransactionPort;
    private final CheckStreakUseCase checkStreakUseCase;

    public CreateTransactionService(LoadUserPort loadUserPort,
                                    SaveTransactionPort saveTransactionPort,
                                    CheckStreakUseCase checkStreakUseCase) {
        this.loadUserPort = loadUserPort;
        this.saveTransactionPort = saveTransactionPort;
        this.checkStreakUseCase = checkStreakUseCase;
    }

    @Override
    public Transaction execute(CreateTransactionCommand command) {
        // Validate user exists
        if (loadUserPort.loadById(command.userId()).isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Transaction transaction = Transaction.builder()
                .userId(command.userId())
                .amount(command.amount())
                .description(command.description())
                .type(command.type())
                .category(command.category())
                .date(command.date())
                .build();

        // transaction.initialize(); - Removed to let JPA handle it

        Transaction savedTransaction = saveTransactionPort.save(transaction);

        // Update streak asynchronously or synchronously depending on requirements
        checkStreakUseCase.execute(command.userId());

        return savedTransaction;
    }
}
