package com.financial.app.application.ports.out;

import com.financial.app.domain.model.Goal;

public interface SaveGoalPort {
    Goal save(Goal goal);
}
