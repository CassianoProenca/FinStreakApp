// FILE: src/main/java/com/financial/app/controllers/TransactionController.java
package com.financial.app.controllers;

import com.financial.app.dto.request.CreateTransactionRequest;
import com.financial.app.dto.response.TransactionResponse;
import com.financial.app.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @RequestBody @Valid CreateTransactionRequest request,
            Authentication authentication,
            @RequestHeader(value = "X-User-Id", required = false) UUID xUserIdHeader
    ) {
        UUID userId = resolveUserId(authentication, xUserIdHeader);
        TransactionResponse response = service.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> listAll(
            Authentication authentication,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC, size = 20) Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) UUID xUserIdHeader
    ) {
        UUID userId = resolveUserId(authentication, xUserIdHeader);
        return ResponseEntity.ok(service.listAll(userId, pageable));
    }

    private UUID resolveUserId(Authentication authentication, UUID xUserIdHeader) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            try {
                return UUID.fromString(authentication.getName());
            } catch (IllegalArgumentException e) {
                // Ignore if authentication name is not a UUID (fallback to header)
            }
        }
        if (xUserIdHeader != null) {
            return xUserIdHeader;
        }
        throw new IllegalStateException("User ID must be provided via Token or X-User-Id header");
    }
}