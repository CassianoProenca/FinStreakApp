package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.CreateGoalUseCase;
import com.financial.app.application.ports.in.DeleteGoalUseCase;
import com.financial.app.application.ports.in.DepositInGoalUseCase;
import com.financial.app.application.ports.in.ListGoalsUseCase;
import com.financial.app.application.ports.in.UpdateGoalUseCase;
import com.financial.app.application.ports.in.WithdrawFromGoalUseCase;
import com.financial.app.application.ports.in.command.CreateGoalCommand;
import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.exception.UnauthorizedAccessException;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateGoalRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.GoalDepositRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.response.GoalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Metas", description = "Endpoints para planejamento financeiro e objetivos")
public class GoalController {

    private final CreateGoalUseCase createGoalUseCase;
    private final ListGoalsUseCase listGoalsUseCase;
    private final UpdateGoalUseCase updateGoalUseCase;
    private final DeleteGoalUseCase deleteGoalUseCase;
    private final DepositInGoalUseCase depositInGoalUseCase;
    private final WithdrawFromGoalUseCase withdrawFromGoalUseCase;
    private final GoalHistoryPort goalHistoryPort;
    private final LoadGoalsPort loadGoalsPort;

    @Operation(
            summary = "Remover meta",
            description = "Exclui permanentemente uma meta. Apenas o dono pode remover.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Meta removida com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "A meta não pertence ao usuário autenticado"),
                    @ApiResponse(responseCode = "404", description = "Meta não encontrada")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID único da meta", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        deleteGoalUseCase.execute(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Criar nova meta",
            description = "Define um objetivo financeiro com valor alvo e data limite.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Meta criada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (campo obrigatório ausente, valor negativo ou data no passado)"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @PostMapping
    public ResponseEntity<GoalResponse> create(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"title\": \"Viagem Japão\", \"targetAmount\": 15000, \"deadline\": \"2027-01-01T00:00:00\", \"iconKey\": \"airplane\"}"))
            )
            @Valid CreateGoalRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        CreateGoalCommand command = new CreateGoalCommand(
                userId,
                request.title(),
                request.targetAmount(),
                request.currentAmount(),
                request.deadline(),
                request.iconKey()
        );

        Goal goal = createGoalUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(goal));
    }

    @Operation(
            summary = "Atualizar meta",
            description = "Permite alterar o título, valor alvo, prazo e ícone de uma meta existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Meta atualizada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "A meta não pertence ao usuário autenticado"),
                    @ApiResponse(responseCode = "404", description = "Meta não encontrada")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> update(
            @Parameter(description = "ID único da meta", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"title\": \"Viagem Europa\", \"targetAmount\": 20000, \"deadline\": \"2027-06-01T00:00:00\", \"iconKey\": \"airplane\"}"))
            )
            @Valid CreateGoalRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        CreateGoalCommand command = new CreateGoalCommand(
                userId,
                request.title(),
                request.targetAmount(),
                request.currentAmount(),
                request.deadline(),
                request.iconKey()
        );

        Goal goal = updateGoalUseCase.execute(userId, id, command);
        return ResponseEntity.ok(toResponse(goal));
    }

    @Operation(
            summary = "Depositar em uma meta",
            description = "Adiciona um valor ao saldo atual de uma meta e registra no histórico. O valor é descontado do saldo disponível do usuário automaticamente via transação GOAL_ALLOCATION.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Valor inválido ou negativo"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "404", description = "Meta não encontrada")
            }
    )
    @PostMapping("/{id}/deposit")
    public ResponseEntity<GoalDeposit> deposit(
            @Parameter(description = "ID único da meta", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"amount\": 500, \"description\": \"Aporte mensal\"}"))
            )
            @Valid GoalDepositRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        GoalDeposit deposit = depositInGoalUseCase.execute(userId, id, request.amount(), request.description());
        return ResponseEntity.ok(deposit);
    }

    @Operation(
            summary = "Resgatar de uma meta",
            description = "Retira um valor do saldo atual de uma meta e devolve ao saldo disponível do usuário via transação GOAL_WITHDRAWAL.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resgate realizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Saldo insuficiente na meta para o valor solicitado"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "404", description = "Meta não encontrada")
            }
    )
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<GoalDeposit> withdraw(
            @Parameter(description = "ID único da meta", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"amount\": 200, \"description\": \"Emergência médica\"}"))
            )
            @Valid GoalDepositRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        GoalDeposit deposit = withdrawFromGoalUseCase.execute(userId, id, request.amount(), request.description());
        return ResponseEntity.ok(deposit);
    }

    @Operation(
            summary = "Buscar meta por ID",
            description = "Retorna os detalhes de uma meta específica do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Meta retornada com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "A meta não pertence ao usuário autenticado"),
                    @ApiResponse(responseCode = "404", description = "Meta não encontrada")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(
            @Parameter(description = "ID único da meta", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Goal goal = loadGoalsPort.loadByUserId(userId).stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));
        return ResponseEntity.ok(toResponse(goal));
    }

    @Operation(
            summary = "Ver histórico da meta",
            description = "Lista todos os depósitos e resgates realizados em uma meta específica, ordenados do mais recente ao mais antigo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "A meta não pertence ao usuário autenticado"),
                    @ApiResponse(responseCode = "404", description = "Meta não encontrada")
            }
    )
    @GetMapping("/{id}/history")
    public ResponseEntity<List<GoalDeposit>> getHistory(
            @Parameter(description = "ID único da meta", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        loadGoalsPort.loadByUserId(userId).stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedAccessException("A meta não pertence ao usuário autenticado"));
        return ResponseEntity.ok(goalHistoryPort.findByGoalId(id));
    }

    @Operation(
            summary = "Listar minhas metas",
            description = "Retorna todas as metas do usuário logado com o progresso atualizado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de metas retornada com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @GetMapping
    public ResponseEntity<List<GoalResponse>> list(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Goal> goals = listGoalsUseCase.execute(userId);

        return ResponseEntity.ok(goals.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
    }

    private GoalResponse toResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.calculateProgress(),
                goal.getDeadline(),
                goal.getStatus(),
                goal.getIconKey()
        );
    }
}
