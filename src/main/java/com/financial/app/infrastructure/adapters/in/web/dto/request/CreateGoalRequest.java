package com.financial.app.infrastructure.adapters.in.web.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateGoalRequest(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotNull(message = "O valor alvo é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal targetAmount,

        BigDecimal currentAmount,

        @NotNull(message = "A data limite é obrigatória")
        @Future(message = "A data limite deve ser no futuro")
        LocalDateTime deadline,

        String iconKey
) {}