package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.ResetPasswordUseCase;
import com.financial.app.application.ports.out.LoadPasswordResetTokenPort;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SavePasswordResetTokenPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.model.PasswordResetToken;
import com.financial.app.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ResetPasswordService implements ResetPasswordUseCase {

    private final LoadPasswordResetTokenPort loadPasswordResetTokenPort;
    private final SavePasswordResetTokenPort savePasswordResetTokenPort;
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(String token, String newPassword) {
        PasswordResetToken resetToken = loadPasswordResetTokenPort.loadByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido ou expirado"));

        if (!resetToken.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido ou expirado");
        }

        User user = loadUserPort.loadById(resetToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        saveUserPort.save(user);

        resetToken.setUsed(true);
        savePasswordResetTokenPort.save(resetToken);
    }
}
