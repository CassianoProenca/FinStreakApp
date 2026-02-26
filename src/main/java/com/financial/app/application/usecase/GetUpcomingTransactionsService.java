package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.GetUpcomingTransactionsUseCase;
import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.out.LoadRecurringTransactionsPort;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.application.ports.out.LoadUpcomingTransactionsPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.infrastructure.adapters.in.web.dto.response.UpcomingTransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetUpcomingTransactionsService implements GetUpcomingTransactionsUseCase {

    private final LoadUpcomingTransactionsPort loadUpcomingTransactionsPort;
    private final LoadRecurringTransactionsPort loadRecurringTransactionsPort;

    public GetUpcomingTransactionsService(LoadUpcomingTransactionsPort loadUpcomingTransactionsPort,
                                          LoadRecurringTransactionsPort loadRecurringTransactionsPort) {
        this.loadUpcomingTransactionsPort = loadUpcomingTransactionsPort;
        this.loadRecurringTransactionsPort = loadRecurringTransactionsPort;
    }

    @Override
    public List<UpcomingTransactionResponse> execute(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        List<UpcomingTransactionResponse> results = new ArrayList<>();

        // Future installment children already saved in DB
        List<Transaction> futureInstallments = loadUpcomingTransactionsPort.loadFutureInstallments(userId, now);
        futureInstallments.stream()
                .map(t -> toResponse(t, false))
                .forEach(results::add);

        // Projections for recurring transactions (next 3 months)
        List<Transaction> recurring = loadRecurringTransactionsPort.loadActiveRecurring().stream()
                .filter(t -> t.getUserId().equals(userId))
                .collect(Collectors.toList());

        for (Transaction rec : recurring) {
            for (int monthOffset = 1; monthOffset <= 3; monthOffset++) {
                LocalDateTime projectedDate = now.plusMonths(monthOffset)
                        .withDayOfMonth(rec.getRepeatDay() != null ? Math.min(rec.getRepeatDay(), now.plusMonths(monthOffset).toLocalDate().lengthOfMonth()) : now.getDayOfMonth());
                if (projectedDate.isAfter(now)) {
                    Transaction projection = Transaction.builder()
                            .id(rec.getId())
                            .userId(rec.getUserId())
                            .amount(rec.getAmount())
                            .description(rec.getDescription())
                            .type(rec.getType())
                            .category(rec.getCategory())
                            .date(projectedDate)
                            .isRecurring(true)
                            .frequency(rec.getFrequency())
                            .repeatDay(rec.getRepeatDay())
                            .iconKey(rec.getIconKey())
                            .build();
                    results.add(toResponse(projection, true));
                }
            }
        }

        results.sort((a, b) -> a.date().compareTo(b.date()));
        return results;
    }

    private UpcomingTransactionResponse toResponse(Transaction t, boolean isProjection) {
        return new UpcomingTransactionResponse(
                t.getId(), t.getAmount(), t.getDescription(), t.getType(), t.getCategory(),
                t.getDate(), t.getCreatedAt(), t.isRecurring(), t.getFrequency(), t.getRepeatDay(),
                t.getIconKey(), t.getGoalId(), t.getTotalInstallments(), t.getCurrentInstallment(),
                isProjection
        );
    }
}
