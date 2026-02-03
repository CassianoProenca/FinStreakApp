package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Goal;
import java.util.List;
import java.util.UUID;

public interface LoadGoalsPort {
    List<Goal> loadByUserId(UUID userId);
}
