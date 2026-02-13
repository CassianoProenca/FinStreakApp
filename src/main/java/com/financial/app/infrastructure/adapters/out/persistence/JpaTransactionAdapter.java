package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.out.CheckTransactionInstancePort;
import com.financial.app.application.ports.out.DeleteTransactionPort;
import com.financial.app.application.ports.out.LoadRecurringTransactionsPort;
import com.financial.app.application.ports.out.LoadTransactionPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.model.PagedResult;
import com.financial.app.domain.model.Transaction;
import com.financial.app.infrastructure.adapters.out.persistence.entity.TransactionEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.TransactionMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.TransactionJpaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JpaTransactionAdapter implements SaveTransactionPort, LoadTransactionPort, LoadRecurringTransactionsPort, CheckTransactionInstancePort, DeleteTransactionPort {

    private final TransactionJpaRepository transactionJpaRepository;

    public JpaTransactionAdapter(TransactionJpaRepository transactionJpaRepository) {
        this.transactionJpaRepository = transactionJpaRepository;
    }

    @Override
    public void deleteById(UUID id) {
        transactionJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Transaction> loadById(UUID id) {
        return transactionJpaRepository.findById(id)
                .map(TransactionMapper::toDomain);
    }

    @Override
    public List<Transaction> loadActiveRecurring() {
        return transactionJpaRepository.findByIsRecurringTrueAndParentTransactionIdIsNull().stream()
                .map(TransactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsInstanceInPeriod(UUID parentId, LocalDateTime start, LocalDateTime end) {
        return transactionJpaRepository.existsByParentTransactionIdAndDateBetween(parentId, start, end);
    }

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() != null) {
            Optional<TransactionEntity> existingEntityOpt = transactionJpaRepository.findById(transaction.getId());
            if (existingEntityOpt.isPresent()) {
                TransactionEntity existingEntity = existingEntityOpt.get();

                existingEntity.setUserId(transaction.getUserId());
                existingEntity.setAmount(transaction.getAmount());
                existingEntity.setDescription(transaction.getDescription());
                existingEntity.setType(transaction.getType());
                existingEntity.setCategory(transaction.getCategory());
                existingEntity.setDate(transaction.getDate());
                existingEntity.setRecurring(transaction.isRecurring());
                existingEntity.setFrequency(transaction.getFrequency());
                existingEntity.setRepeatDay(transaction.getRepeatDay());
                existingEntity.setIconKey(transaction.getIconKey());
                existingEntity.setParentTransactionId(transaction.getParentTransactionId());

                TransactionEntity savedEntity = transactionJpaRepository.save(existingEntity);
                return TransactionMapper.toDomain(savedEntity);
            }
        }

        TransactionEntity entity = TransactionMapper.toEntity(transaction);
        TransactionEntity savedEntity = transactionJpaRepository.save(entity);
        return TransactionMapper.toDomain(savedEntity);
    }

    @Override
    public PagedResult<Transaction> loadByQuery(TransactionQuery query) {
        Specification<TransactionEntity> spec = buildSpecification(query);
        Pageable pageable = PageRequest.of(query.page(), query.size(), Sort.by("date").descending());

        Page<TransactionEntity> page = transactionJpaRepository.findAll(spec, pageable);

        List<Transaction> content = page.getContent().stream()
                .map(TransactionMapper::toDomain)
                .collect(Collectors.toList());

        return new PagedResult<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public List<Transaction> loadAllByQuery(TransactionQuery query) {
        Specification<TransactionEntity> spec = buildSpecification(query);
        return transactionJpaRepository.findAll(spec).stream()
                .map(TransactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    private Specification<TransactionEntity> buildSpecification(TransactionQuery query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("userId"), query.userId()));

            if (query.startDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), query.startDate()));
            }

            if (query.endDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), query.endDate()));
            }

            if (query.type() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), query.type()));
            }

            if (query.category() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), query.category()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
