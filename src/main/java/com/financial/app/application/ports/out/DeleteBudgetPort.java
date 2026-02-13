package com.financial.app.application.ports.out;

import java.util.UUID;

public interface DeleteBudgetPort {
    void deleteById(UUID id);
}
