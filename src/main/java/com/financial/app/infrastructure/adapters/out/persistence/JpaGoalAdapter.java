package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.domain.model.Goal;
import com.financial.app.infrastructure.adapters.out.persistence.entity.GoalEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.GoalMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.GoalJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JpaGoalAdapter implements SaveGoalPort, LoadGoalsPort {

    private final GoalJpaRepository repository;

    public JpaGoalAdapter(GoalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Goal save(Goal goal) {
        // If goal has an ID, load and update the existing entity
        if (goal.getId() != null) {
            Optional<GoalEntity> existingEntityOpt = repository.findById(goal.getId());
            if (existingEntityOpt.isPresent()) {
                GoalEntity existingEntity = existingEntityOpt.get();

                // Update fields
                existingEntity.setUserId(goal.getUserId());
                existingEntity.setTitle(goal.getTitle());
                existingEntity.setTargetAmount(goal.getTargetAmount());
                existingEntity.setCurrentAmount(goal.getCurrentAmount());
                existingEntity.setDeadline(goal.getDeadline());
                existingEntity.setStatus(goal.getStatus());
                existingEntity.setIcon(goal.getIcon());

                GoalEntity savedEntity = repository.save(existingEntity);
                return GoalMapper.toDomain(savedEntity);
            }
        }

        // For new goals, create a new entity
        GoalEntity entity = GoalMapper.toEntity(goal);
        GoalEntity savedEntity = repository.save(entity);
        return GoalMapper.toDomain(savedEntity);
    }

    @Override
    public List<Goal> loadByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(GoalMapper::toDomain)
                .collect(Collectors.toList());
    }
}
