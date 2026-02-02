package com.financial.app.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID userId,
        BigDecimal amount,
        LocalDateTime date
) {}