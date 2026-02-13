package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.DeleteTransactionUseCase;
import com.financial.app.application.ports.in.UpdateTransactionUseCase;
import com.financial.app.application.ports.in.ListTransactionsUseCase;
import com.financial.app.application.ports.in.CreateTransactionUseCase;
import com.financial.app.application.ports.in.TransactionQuery;
import com.financial.app.application.ports.in.command.CreateTransactionCommand;
import com.financial.app.domain.model.PagedResult;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateTransactionRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Gerenciamento de receitas e despesas")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    @Operation(
            summary = "Remover transação",
            description = "Exclui permanentemente uma transação do sistema.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Transação removida com sucesso")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        deleteTransactionUseCase.execute(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Criar nova transação",
            description = "Registra uma entrada ou saída financeira. Suporta recorrência e ícones customizados.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transação criada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            }
    )
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Detalhes da transação",
                    content = @Content(examples = @ExampleObject(value = "{\"amount\": 45.90, \"description\": \"iFood\", \"type\": \"EXPENSE\", \"category\": \"FOOD\", \"date\": \"2026-02-13T14:00:00\", \"isRecurring\": false, \"iconKey\": \"hamburger\"}"))
            )
            @Valid CreateTransactionRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        CreateTransactionCommand command = new CreateTransactionCommand(
                userId,
                request.amount(),
                request.description(),
                request.type(),
                request.category(),
                request.date(),
                request.isRecurring(),
                request.frequency(),
                request.repeatDay(),
                request.iconKey()
        );

        Transaction transaction = createTransactionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    @Operation(
            summary = "Atualizar transação",
            description = "Edita os dados de uma transação existente. Apenas o dono pode editar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transação atualizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
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
                request.date(),
                request.isRecurring(),
                request.frequency(),
                request.repeatDay(),
                request.iconKey()
        );

        Transaction transaction = updateTransactionUseCase.execute(userId, id, command);
        return ResponseEntity.ok(toResponse(transaction));
    }

    @Operation(
            summary = "Listar transações com filtros",
            description = "Retorna uma lista paginada de transações do usuário, permitindo filtrar por data, tipo e categoria.",
            parameters = {
                    @Parameter(name = "startDate", description = "Data inicial (ISO 8601)", example = "2026-02-01T00:00:00"),
                    @Parameter(name = "endDate", description = "Data final (ISO 8601)", example = "2026-02-28T23:59:59"),
                    @Parameter(name = "page", description = "Número da página (0-N)", example = "0"),
                    @Parameter(name = "size", description = "Quantidade de itens por página", example = "10")
            }
    )
    @GetMapping
    public ResponseEntity<PagedResult<TransactionResponse>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        TransactionQuery query = new TransactionQuery(
                userId, startDate, endDate, type, category, page, size
        );

        PagedResult<Transaction> result = listTransactionsUseCase.execute(query);

        List<TransactionResponse> content = result.content().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        PagedResult<TransactionResponse> response = new PagedResult<>(
                content,
                result.pageNumber(),
                result.pageSize(),
                result.totalElements(),
                result.totalPages(),
                result.isLast()
        );

        return ResponseEntity.ok(response);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDate(),
                transaction.getCreatedAt(),
                transaction.isRecurring(),
                transaction.getFrequency(),
                transaction.getRepeatDay(),
                transaction.getIconKey()
        );
    }
}
