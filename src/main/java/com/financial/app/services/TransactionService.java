package com.financial.app.services;

import com.financial.app.dto.request.CreateTransactionRequest;
import com.financial.app.dto.response.TransactionResponse;
import com.financial.app.events.TransactionCreatedEvent;
import com.financial.app.mappers.TransactionMapper;
import com.financial.app.model.Transaction;
import com.financial.app.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransactionResponse create(UUID userId, CreateTransactionRequest request) {
        Transaction entity = mapper.toEntity(request, userId);

        Transaction savedEntity = repository.save(entity);

        eventPublisher.publishEvent(new TransactionCreatedEvent(
                savedEntity.getUserId(),
                savedEntity.getAmount(),
                savedEntity.getDate()
        ));

        return mapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> listAll(UUID userId, Pageable pageable) {
        return repository.findByUserIdOrderByDateDesc(userId, pageable)
                .map(mapper::toResponse);
    }
}