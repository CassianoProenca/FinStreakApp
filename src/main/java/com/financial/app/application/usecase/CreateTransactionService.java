package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CheckStreakUseCase;
import com.financial.app.application.ports.in.CreateTransactionUseCase;
import com.financial.app.application.ports.in.GetDailyMissionsUseCase;
import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class CreateTransactionService implements CreateTransactionUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveTransactionPort saveTransactionPort;
    private final CheckStreakUseCase checkStreakUseCase;
    private final BudgetService budgetService;
    private final GetDailyMissionsUseCase getDailyMissionsUseCase;

    public CreateTransactionService(LoadUserPort loadUserPort,
                                    SaveTransactionPort saveTransactionPort,
                                    CheckStreakUseCase checkStreakUseCase,
                                    BudgetService budgetService,
                                    GetDailyMissionsUseCase getDailyMissionsUseCase) {
        this.loadUserPort = loadUserPort;
        this.saveTransactionPort = saveTransactionPort;
        this.checkStreakUseCase = checkStreakUseCase;
        this.budgetService = budgetService;
        this.getDailyMissionsUseCase = getDailyMissionsUseCase;
    }

    @Override
    public Transaction execute(CreateTransactionCommand command) {
        if (loadUserPort.loadById(command.userId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        int totalInstallments = (command.installments() != null && command.installments() > 1) ? command.installments() : 1;
        boolean isInstallment = totalInstallments > 1;

        Transaction.TransactionBuilder parentBuilder = Transaction.builder()
                .userId(command.userId())
                .amount(command.amount())
                .description(command.description())
                .type(command.type())
                .category(command.category())
                .date(command.date() != null ? command.date() : LocalDateTime.now())
                .isRecurring(!isInstallment && command.isRecurring())
                .frequency(!isInstallment ? command.frequency() : null)
                .repeatDay(!isInstallment ? command.repeatDay() : null)
                .iconKey(command.iconKey());

        if (isInstallment) {
            parentBuilder.totalInstallments(totalInstallments).currentInstallment(1);
        }

        Transaction savedParent = saveTransactionPort.save(parentBuilder.build());

        if (isInstallment) {
            LocalDateTime parentDate = savedParent.getDate();
            for (int i = 2; i <= totalInstallments; i++) {
                Transaction child = Transaction.builder()
                        .userId(command.userId())
                        .amount(command.amount())
                        .description(command.description())
                        .type(command.type())
                        .category(command.category())
                        .date(parentDate.plusMonths(i - 1))
                        .isRecurring(false)
                        .iconKey(command.iconKey())
                        .parentTransactionId(savedParent.getId())
                        .totalInstallments(totalInstallments)
                        .currentInstallment(i)
                        .build();
                saveTransactionPort.save(child);
            }
        }

        checkStreakUseCase.execute(command.userId());

        // Fire BUDGET_ALERT if this is an expense (#11)
        if (command.type() == TransactionType.EXPENSE && command.category() != null && command.date() != null) {
            budgetService.checkAndAlertBudget(command.userId(),
                    command.date().getMonthValue(), command.date().getYear(), command.category());
        }

        // Check daily missions after transaction
        try {
            getDailyMissionsUseCase.execute(command.userId());
        } catch (Exception e) {
            log.warn("Daily mission check failed for user {}: {}", command.userId(), e.getMessage());
        }

        return savedParent;
    }
}
