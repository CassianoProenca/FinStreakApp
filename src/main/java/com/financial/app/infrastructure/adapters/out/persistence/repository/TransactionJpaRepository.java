package com.financial.app.infrastructure.adapters.out.persistence.repository;

import com.financial.app.infrastructure.adapters.out.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID>, JpaSpecificationExecutor<TransactionEntity> {
    
    List<TransactionEntity> findByIsRecurringTrueAndParentTransactionIdIsNull();

    boolean existsByParentTransactionIdAndDateBetween(UUID parentId, LocalDateTime start, LocalDateTime end);

    List<TransactionEntity> findByUserIdAndParentTransactionIdIsNotNullAndDateAfterOrderByDateAsc(UUID userId, LocalDateTime after);

    @Modifying
    void deleteByParentTransactionId(UUID parentTransactionId);

    List<TransactionEntity> findByParentTransactionId(UUID parentTransactionId);
}
