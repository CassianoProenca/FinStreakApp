package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.DeleteTransactionUseCase;
import com.financial.app.application.ports.out.DeleteTransactionPort;
import com.financial.app.application.ports.out.LoadTransactionPort;
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
public class DeleteTransactionService implements DeleteTransactionUseCase {

    private final LoadTransactionPort loadTransactionPort;
    private final DeleteTransactionPort deleteTransactionPort;

    @Override
    public void execute(UUID userId, UUID transactionId) {
        Transaction transaction = loadTransactionPort.loadById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para excluir esta transação");
        }

        deleteTransactionPort.deleteById(transactionId);
    }
}
