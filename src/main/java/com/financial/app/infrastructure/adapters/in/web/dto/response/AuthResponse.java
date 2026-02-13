package com.financial.app.infrastructure.adapters.in.web.dto.response;

import java.math.BigDecimal;

public record AuthResponse(
        String token,
        String name,
        String avatarUrl,
        boolean onboardingCompleted,
        BigDecimal monthlyIncome
) {}
