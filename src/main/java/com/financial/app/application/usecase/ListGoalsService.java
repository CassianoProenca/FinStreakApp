package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.ListGoalsUseCase;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.domain.model.Goal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListGoalsService implements ListGoalsUseCase {

    private final LoadGoalsPort loadGoalsPort;

    public ListGoalsService(LoadGoalsPort loadGoalsPort) {
        this.loadGoalsPort = loadGoalsPort;
    }

    @Override
    public List<Goal> execute(UUID userId) {
        return loadGoalsPort.loadByUserId(userId);
    }
}
