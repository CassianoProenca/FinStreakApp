package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.Transaction;
import com.financial.app.infrastructure.adapters.out.persistence.entity.TransactionEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.TransactionMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.TransactionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JpaTransactionAdapter implements SaveTransactionPort, LoadTransactionPort {

    private final TransactionJpaRepository transactionJpaRepository;

    public JpaTransactionAdapter(TransactionJpaRepository transactionJpaRepository) {
        this.transactionJpaRepository = transactionJpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        // If transaction has an ID, load and update the existing entity
        if (transaction.getId() != null) {
            Optional<TransactionEntity> existingEntityOpt = transactionJpaRepository.findById(transaction.getId());
            if (existingEntityOpt.isPresent()) {
                TransactionEntity existingEntity = existingEntityOpt.get();

                // Update fields
                existingEntity.setUserId(transaction.getUserId());
                existingEntity.setAmount(transaction.getAmount());
                existingEntity.setDescription(transaction.getDescription());
                existingEntity.setType(transaction.getType());
                existingEntity.setCategory(transaction.getCategory());
                existingEntity.setDate(transaction.getDate());

                TransactionEntity savedEntity = transactionJpaRepository.save(existingEntity);
                return TransactionMapper.toDomain(savedEntity);
            }
        }

        // For new transactions, create a new entity
        TransactionEntity entity = TransactionMapper.toEntity(transaction);
        TransactionEntity savedEntity = transactionJpaRepository.save(entity);
        return TransactionMapper.toDomain(savedEntity);
    }

    @Override
    public List<Transaction> loadByUserId(UUID userId) {
        return transactionJpaRepository.findByUserId(userId).stream()
                .map(TransactionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
