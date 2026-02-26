package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.UpdateUserProfileUseCase;
import com.financial.app.application.ports.in.command.UpdateUserProfileCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserProfileService implements UpdateUserProfileUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    @Override
    public User execute(UpdateUserProfileCommand command) {
        User user = loadUserPort.loadById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (command.name() != null && !command.name().isBlank()) {
            user.setName(command.name());
        }

        if (command.avatarUrl() != null) {
            user.setAvatarUrl(command.avatarUrl());
        }

        // Password changes are not allowed via this endpoint.
        // Use POST /api/auth/change-password which requires the current password.

        return saveUserPort.save(user);
    }
}
