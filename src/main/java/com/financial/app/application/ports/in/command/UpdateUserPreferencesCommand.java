package com.financial.app.application.ports.in.command;

import com.financial.app.domain.model.enums.Theme;

import java.util.UUID;

public record UpdateUserPreferencesCommand(
    UUID userId,
    Theme theme,
    boolean notificationsEnabled
) {}
