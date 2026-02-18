package com.financial.app.application.ports.in;

public interface ResetPasswordUseCase {
    void execute(String token, String newPassword);
}
