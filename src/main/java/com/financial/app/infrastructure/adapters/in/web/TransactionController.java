package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.CreateTransactionUseCase;
import com.financial.app.application.ports.in.ListTransactionsUseCase;
import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.domain.model.Transaction;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateTransactionRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @RequestBody @Valid CreateTransactionRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        CreateTransactionCommand command = new CreateTransactionCommand(
                userId,
                request.amount(),
                request.description(),
                request.type(),
                request.category(),
                request.date()
        );

        Transaction transaction = createTransactionUseCase.execute(command);
        
        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDate(),
                transaction.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listAll(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Transaction> transactions = listTransactionsUseCase.execute(userId);
        
        List<TransactionResponse> response = transactions.stream()
                .map(t -> new TransactionResponse(
                        t.getId(),
                        t.getAmount(),
                        t.getDescription(),
                        t.getType(),
                        t.getCategory(),
                        t.getDate(),
                        t.getCreatedAt()
                ))
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(response);
    }
}