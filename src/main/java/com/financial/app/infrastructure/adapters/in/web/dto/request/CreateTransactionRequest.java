// FILE: src/main/java/com/financial/app/dto/request/CreateTransactionRequest.java
package com.financial.app.infrastructure.adapters.in.web.dto.request;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTransactionRequest(

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal amount,

        @NotBlank(message = "A descrição não pode estar vazia")
        @Size(min = 3, max = 100, message = "A descrição deve ter entre 3 e 100 caracteres")
        String description,

        @NotNull(message = "O tipo da transação é obrigatório")
        TransactionType type,

        @NotNull(message = "A categoria é obrigatória")
        TransactionCategory category,

        // Opcional: Se o user não mandar data, o sistema assume "agora" no Mapper
        LocalDateTime date
) {}