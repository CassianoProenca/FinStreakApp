package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.BudgetUseCase;
import com.financial.app.application.ports.in.DeleteBudgetUseCase;
import com.financial.app.application.ports.in.command.CreateBudgetCommand;
import com.financial.app.domain.model.Budget;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateBudgetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Orçamentos", description = "Definição de limites de gastos por categoria")
public class BudgetController {

    private final BudgetUseCase budgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;

    @Operation(
            summary = "Remover orçamento",
            description = "Exclui o limite de gastos para uma categoria no período informado.",
            parameters = {
                    @Parameter(name = "category", description = "Categoria do orçamento a remover", required = true, example = "FOOD"),
                    @Parameter(name = "month", description = "Mês de referência (1-12)", required = true, example = "2"),
                    @Parameter(name = "year", description = "Ano de referência", required = true, example = "2026")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Orçamento removido com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "404", description = "Orçamento não encontrado para a categoria/período informados")
            }
    )
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam TransactionCategory category,
            @RequestParam Integer month,
            @RequestParam Integer year,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        deleteBudgetUseCase.execute(userId, category, month, year);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Definir ou atualizar orçamento",
            description = "Define um limite de gastos para uma categoria em um mês específico. Se já existir, atualiza o valor.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orçamento salvo com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (categoria ausente, valor negativo ou mês fora do range 1-12)"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @PostMapping
    public ResponseEntity<Budget> createOrUpdate(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"category\": \"FOOD\", \"limitAmount\": 800, \"month\": 2, \"year\": 2026}"))
            )
            @Valid CreateBudgetRequest request,
            Authentication authentication) {
        return processUpsert(request, authentication);
    }

    @Operation(
            summary = "Atualizar orçamento existente",
            description = "Alias para o POST. Atualiza o limite de uma categoria já existente no período.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orçamento atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @PutMapping
    public ResponseEntity<Budget> update(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"category\": \"FOOD\", \"limitAmount\": 1000, \"month\": 2, \"year\": 2026}"))
            )
            @Valid CreateBudgetRequest request,
            Authentication authentication) {
        return processUpsert(request, authentication);
    }

    private ResponseEntity<Budget> processUpsert(CreateBudgetRequest request, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        int month = request.month() != null ? request.month() : LocalDate.now().getMonthValue();
        int year = request.year() != null ? request.year() : LocalDate.now().getYear();

        CreateBudgetCommand command = new CreateBudgetCommand(
                userId, request.category(), request.limitAmount(), month, year
        );

        return ResponseEntity.ok(budgetUseCase.createOrUpdate(command));
    }

    @Operation(
            summary = "Listar orçamentos do mês",
            description = "Retorna todos os limites definidos para o período. Se month/year não forem informados, usa o mês atual.",
            parameters = {
                    @Parameter(name = "month", description = "Mês de referência (1-12). Padrão: mês atual", example = "2"),
                    @Parameter(name = "year", description = "Ano de referência. Padrão: ano atual", example = "2026")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de orçamentos do período"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @GetMapping
    public ResponseEntity<List<Budget>> list(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        int queryMonth = month != null ? month : LocalDate.now().getMonthValue();
        int queryYear = year != null ? year : LocalDate.now().getYear();

        return ResponseEntity.ok(budgetUseCase.listByUserAndPeriod(userId, queryMonth, queryYear));
    }
}
