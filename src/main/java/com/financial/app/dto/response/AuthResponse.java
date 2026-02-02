package com.financial.app.dto.response;

public record AuthResponse(
        String token,
        String name
) {}
