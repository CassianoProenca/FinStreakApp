package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.in.CreateTransactionUseCase;
import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CreateTransactionService implements CreateTransactionUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveTransactionPort saveTransactionPort;
    private final CheckStreakUseCase checkStreakUseCase;
    private final BudgetService budgetService;

    public CreateTransactionService(LoadUserPort loadUserPort,
                                    SaveTransactionPort saveTransactionPort,
                                    CheckStreakUseCase checkStreakUseCase,
                                    BudgetService budgetService) {
        this.loadUserPort = loadUserPort;
        this.saveTransactionPort = saveTransactionPort;
        this.checkStreakUseCase = checkStreakUseCase;
        this.budgetService = budgetService;
    }

    @Override
    public Transaction execute(CreateTransactionCommand command) {
        if (loadUserPort.loadById(command.userId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        Transaction transaction = Transaction.builder()
                .userId(command.userId())
                .amount(command.amount())
                .description(command.description())
                .type(command.type())
                .category(command.category())
                .date(command.date())
                .isRecurring(command.isRecurring())
                .frequency(command.frequency())
                .repeatDay(command.repeatDay())
                .iconKey(command.iconKey())
                .build();

        Transaction savedTransaction = saveTransactionPort.save(transaction);

        checkStreakUseCase.execute(command.userId());

        if (TransactionType.EXPENSE.equals(savedTransaction.getType())) {
            int month = savedTransaction.getDate().getMonthValue();
            int year = savedTransaction.getDate().getYear();
            budgetService.checkAndAlertBudget(command.userId(), savedTransaction.getCategory(), month, year);
        }

        return savedTransaction;
    }
}
