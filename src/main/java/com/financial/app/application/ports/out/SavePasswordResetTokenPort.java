package com.financial.app.application.ports.out;

import com.financial.app.domain.model.PasswordResetToken;

public interface SavePasswordResetTokenPort {
    PasswordResetToken save(PasswordResetToken token);
}
