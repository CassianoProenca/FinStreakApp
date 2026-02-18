package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.ForgotPasswordUseCase;
import com.financial.app.application.ports.out.EmailPort;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SavePasswordResetTokenPort;
import com.financial.app.domain.model.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ForgotPasswordService implements ForgotPasswordUseCase {

    private final LoadUserPort loadUserPort;
    private final SavePasswordResetTokenPort savePasswordResetTokenPort;
    private final EmailPort emailPort;

    @Override
    public void execute(String email) {
        loadUserPort.loadByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .userId(user.getId())
                    .token(token)
                    .expiresAt(LocalDateTime.now().plusMinutes(15))
                    .used(false)
                    .build();
            savePasswordResetTokenPort.save(resetToken);
            emailPort.sendPasswordReset(email, token);
        });
        // Always succeed — never reveal if email exists
    }
}
