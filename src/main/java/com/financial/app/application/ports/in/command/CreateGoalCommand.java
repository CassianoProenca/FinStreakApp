package com.financial.app.application.ports.in.command;

import com.financial.app.domain.model.enums.GoalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateGoalCommand(
    UUID userId,
    String title,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    LocalDate deadline,
    String icon
) {}
