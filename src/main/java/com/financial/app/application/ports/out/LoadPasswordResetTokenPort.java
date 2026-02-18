package com.financial.app.application.ports.out;

import com.financial.app.domain.model.PasswordResetToken;

import java.util.Optional;

public interface LoadPasswordResetTokenPort {
    Optional<PasswordResetToken> loadByToken(String token);
}
