// FILE: src/main/java/com/financial/app/dto/response/TransactionResponse.java
package com.financial.app.dto.response;

import com.financial.app.model.enums.TransactionCategory;
import com.financial.app.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        BigDecimal amount,
        String description,
        TransactionType type,
        TransactionCategory category,
        LocalDateTime date,
        LocalDateTime createdAt
) {}