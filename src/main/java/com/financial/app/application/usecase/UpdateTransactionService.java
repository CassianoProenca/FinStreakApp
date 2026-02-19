package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.UpdateTransactionUseCase;
import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.exception.UnauthorizedAccessException;
import com.financial.app.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTransactionService implements UpdateTransactionUseCase {

    private final LoadTransactionPort loadTransactionPort;
    private final SaveTransactionPort saveTransactionPort;

    @Override
    public Transaction execute(UUID userId, UUID transactionId, CreateTransactionCommand command) {
        Transaction transaction = loadTransactionPort.loadById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para alterar esta transação");
        }

        // Update fields
        transaction.setAmount(command.amount());
        transaction.setDescription(command.description());
        transaction.setType(command.type());
        transaction.setCategory(command.category());
        transaction.setDate(command.date() != null ? command.date() : transaction.getDate());
        transaction.setRecurring(command.isRecurring());
        transaction.setFrequency(command.frequency());
        transaction.setRepeatDay(command.repeatDay());
        transaction.setIconKey(command.iconKey());

        return saveTransactionPort.save(transaction);
    }
}
