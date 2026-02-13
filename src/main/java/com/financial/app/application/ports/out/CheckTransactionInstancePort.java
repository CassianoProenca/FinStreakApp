package com.financial.app.application.ports.out;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CheckTransactionInstancePort {
    boolean existsInstanceInPeriod(UUID parentId, LocalDateTime start, LocalDateTime end);
}
