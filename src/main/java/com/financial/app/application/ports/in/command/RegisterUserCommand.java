package com.financial.app.application.ports.in.command;

public record RegisterUserCommand(
    String name,
    String email,
    String password
) {}
