package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.ProcessRecurringTransactionsUseCase;
import com.financial.app.application.ports.out.CheckTransactionInstancePort;
import com.financial.app.application.ports.out.LoadRecurringTransactionsPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
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

        for (Transaction parent : parentTransactions) {
            String frequency = parent.getFrequency();
            if ("WEEKLY".equalsIgnoreCase(frequency)) {
                processWeekly(parent, now);
            } else {
                // Default: MONTHLY
                processMonthly(parent, now);
            }
        }
    }

    private void processMonthly(Transaction parent, LocalDateTime now) {
        LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear())).with(LocalTime.MAX);

        int repeatDay = parent.getRepeatDay() != null ? parent.getRepeatDay() : 1;
        boolean isTimeToProcess = now.getDayOfMonth() >= repeatDay;

        if (isTimeToProcess) {
            boolean alreadyExists = checkTransactionInstancePort.existsInstanceInPeriod(parent.getId(), startOfMonth, endOfMonth);
            if (!alreadyExists) {
                int day = repeatDay;
                int maxDay = now.getMonth().length(now.toLocalDate().isLeapYear());
                if (day > maxDay) day = maxDay;
                saveTransactionPort.save(buildInstance(parent, now.withDayOfMonth(day).with(LocalTime.NOON)));
            }
        }
    }

    private void processWeekly(Transaction parent, LocalDateTime now) {
        // Check the current week window (Mon 00:00 to Sun 23:59)
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        LocalDateTime endOfWeek = now.with(DayOfWeek.SUNDAY).with(LocalTime.MAX);

        boolean alreadyExists = checkTransactionInstancePort.existsInstanceInPeriod(parent.getId(), startOfWeek, endOfWeek);
        if (!alreadyExists) {
            saveTransactionPort.save(buildInstance(parent, now.with(LocalTime.NOON)));
        }
    }

    private Transaction buildInstance(Transaction parent, LocalDateTime date) {
        return Transaction.builder()
                .userId(parent.getUserId())
                .amount(parent.getAmount())
                .description(parent.getDescription())
                .type(parent.getType())
                .category(parent.getCategory())
                .date(date)
                .isRecurring(false)
                .iconKey(parent.getIconKey())
                .parentTransactionId(parent.getId())
                .build();
    }
}
