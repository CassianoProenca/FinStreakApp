package com.financial.app.application.ports.in.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateGoalCommand(
    UUID userId,
    String title,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    LocalDateTime deadline,
    String iconKey
) {}
