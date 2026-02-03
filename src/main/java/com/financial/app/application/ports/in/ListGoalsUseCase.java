package com.financial.app.application.ports.in;

import com.financial.app.domain.model.Goal;
import java.util.List;
import java.util.UUID;

public interface ListGoalsUseCase {
    List<Goal> execute(UUID userId);
}
