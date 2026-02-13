package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.infrastructure.adapters.out.persistence.entity.GoalHistoryEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.GoalHistoryMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.GoalHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaGoalHistoryAdapter implements GoalHistoryPort {

    private final GoalHistoryJpaRepository repository;

    @Override
    public GoalDeposit save(GoalDeposit deposit) {
        GoalHistoryEntity entity = GoalHistoryMapper.toEntity(deposit);
        return GoalHistoryMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<GoalDeposit> findByGoalId(UUID goalId) {
        return repository.findByGoalIdOrderByTransactionDateDesc(goalId).stream()
                .map(GoalHistoryMapper::toDomain)
                .collect(Collectors.toList());
    }
}
