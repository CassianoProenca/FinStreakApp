package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadPasswordResetTokenPort;
import com.financial.app.application.ports.out.SavePasswordResetTokenPort;
import com.financial.app.domain.model.PasswordResetToken;
import com.financial.app.infrastructure.adapters.out.persistence.entity.PasswordResetTokenEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.PasswordResetTokenMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.PasswordResetTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPasswordResetTokenAdapter implements SavePasswordResetTokenPort, LoadPasswordResetTokenPort {

    private final PasswordResetTokenJpaRepository repository;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenEntity entity = PasswordResetTokenMapper.toEntity(token);
        return PasswordResetTokenMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<PasswordResetToken> loadByToken(String token) {
        return repository.findByToken(token)
                .map(PasswordResetTokenMapper::toDomain);
    }
}
