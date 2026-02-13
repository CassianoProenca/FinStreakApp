package com.financial.app.infrastructure.adapters.in.web.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    String name,
    
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    String password,
    
    String avatarUrl
) {}
