package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.ProcessRecurringTransactionsUseCase;
import com.financial.app.application.ports.out.CheckTransactionInstancePort;
import com.financial.app.application.ports.out.LoadRecurringTransactionsPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class ProcessRecurringTransactionsService implements ProcessRecurringTransactionsUseCase {

    private final LoadRecurringTransactionsPort loadRecurringTransactionsPort;
    private final CheckTransactionInstancePort checkTransactionInstancePort;
    private final SaveTransactionPort saveTransactionPort;

    public ProcessRecurringTransactionsService(
            LoadRecurringTransactionsPort loadRecurringTransactionsPort,
            CheckTransactionInstancePort checkTransactionInstancePort,
            SaveTransactionPort saveTransactionPort) {
        this.loadRecurringTransactionsPort = loadRecurringTransactionsPort;
        this.checkTransactionInstancePort = checkTransactionInstancePort;
        this.saveTransactionPort = saveTransactionPort;
    }

    @Override
    public void execute() {
        List<Transaction> parentTransactions = loadRecurringTransactionsPort.loadActiveRecurring();
        LocalDateTime now = LocalDateTime.now();
        
        // Define o início e fim do mês atual para checagem
        LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear())).with(LocalTime.MAX);

        for (Transaction parent : parentTransactions) {
            // Se hoje for o dia de repetir OU se o dia já passou mas ainda não criamos a instância do mês
            boolean isTimeToProcess = now.getDayOfMonth() >= (parent.getRepeatDay() != null ? parent.getRepeatDay() : 1);
            
            if (isTimeToProcess) {
                boolean alreadyExists = checkTransactionInstancePort.existsInstanceInPeriod(parent.getId(), startOfMonth, endOfMonth);
                
                if (!alreadyExists) {
                    createInstanceForCurrentMonth(parent, now);
                }
            }
        }
    }

    private void createInstanceForCurrentMonth(Transaction parent, LocalDateTime now) {
        int day = parent.getRepeatDay() != null ? parent.getRepeatDay() : parent.getDate().getDayOfMonth();
        // Garante que o dia não estoure o mês atual (ex: dia 31 em fevereiro)
        int maxDayOfMonth = now.getMonth().length(now.toLocalDate().isLeapYear());
        if (day > maxDayOfMonth) day = maxDayOfMonth;

        Transaction instance = Transaction.builder()
                .userId(parent.getUserId())
                .amount(parent.getAmount())
                .description(parent.getDescription())
                .type(parent.getType())
                .category(parent.getCategory())
                .date(now.withDayOfMonth(day).with(LocalTime.NOON))
                .isRecurring(false) // A instância em si não é recorrente, ela é o registro final
                .iconKey(parent.getIconKey())
                .parentTransactionId(parent.getId())
                .build();

        saveTransactionPort.save(instance);
    }
}
