package com.financial.app.infrastructure.adapters.out.job;

import com.financial.app.application.ports.in.ProcessRecurringTransactionsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecurrenceJob {

    private final ProcessRecurringTransactionsUseCase processRecurringTransactionsUseCase;

    // Roda todo dia à meia-noite (cron: segundo minuto hora dia mes semana)
    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        log.info("Iniciando processamento de transações recorrentes...");
        try {
            processRecurringTransactionsUseCase.execute();
            log.info("Processamento de recorrência finalizado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao processar transações recorrentes", e);
        }
    }
}
