package com.financial.app.application.ports.in;

import java.util.UUID;

public interface DeleteGoalUseCase {
    void execute(UUID userId, UUID goalId);
}
