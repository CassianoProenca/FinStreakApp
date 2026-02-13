package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.BudgetPort;
import com.financial.app.application.ports.out.DeleteBudgetPort;
import com.financial.app.domain.model.Budget;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.infrastructure.adapters.out.persistence.entity.BudgetEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.BudgetMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.BudgetJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaBudgetAdapter implements BudgetPort, DeleteBudgetPort {

    private final BudgetJpaRepository repository;

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Budget save(Budget budget) {
        BudgetEntity entity = BudgetMapper.toEntity(budget);
        return BudgetMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Budget> findByUserCategoryAndPeriod(UUID userId, TransactionCategory category, int month, int year) {
        return repository.findByUserIdAndCategoryAndMonthAndYear(userId, category, month, year)
                .map(BudgetMapper::toDomain);
    }

    @Override
    public List<Budget> findByUserAndPeriod(UUID userId, int month, int year) {
        return repository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(BudgetMapper::toDomain)
                .collect(Collectors.toList());
    }
}
