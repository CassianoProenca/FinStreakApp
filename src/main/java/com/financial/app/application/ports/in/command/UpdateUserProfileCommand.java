package com.financial.app.application.ports.in.command;

import java.util.UUID;

public record UpdateUserProfileCommand(
    UUID userId,
    String name,
    String password,
    String avatarUrl
) {}
