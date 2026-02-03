package com.financial.app.infrastructure.adapters.in.web.dto.response;

public record AuthResponse(
        String token,
        String name,
        boolean onboardingCompleted
) {}
