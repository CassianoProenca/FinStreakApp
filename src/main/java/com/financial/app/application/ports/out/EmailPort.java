package com.financial.app.application.ports.out;

public interface EmailPort {
    void sendPasswordReset(String toEmail, String resetToken);
}
