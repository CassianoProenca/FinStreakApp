package com.financial.app.infrastructure.adapters.out.notification;

import com.financial.app.application.ports.out.EmailPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogEmailAdapter implements EmailPort {

    @Override
    public void sendPasswordReset(String toEmail, String resetToken) {
        log.info("PASSWORD RESET para {}: token={}", toEmail, resetToken);
    }
}
