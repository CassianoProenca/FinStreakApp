package com.financial.app.application.ports.in;

import java.util.UUID;

public interface CheckStreakUseCase {
    void execute(UUID userId);
}
