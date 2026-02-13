package com.financial.app.application.ports.out;

import java.util.UUID;

public interface DeleteTransactionPort {
    void deleteById(UUID id);
}
