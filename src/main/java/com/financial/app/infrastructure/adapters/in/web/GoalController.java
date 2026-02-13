package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.CreateGoalUseCase;
import com.financial.app.application.ports.in.DeleteGoalUseCase;
import com.financial.app.application.ports.in.DepositInGoalUseCase;
import com.financial.app.application.ports.in.ListGoalsUseCase;
import com.financial.app.application.ports.in.UpdateGoalUseCase;
import com.financial.app.application.ports.in.command.CreateGoalCommand;
import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateGoalRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.GoalDepositRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.response.GoalResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    private final GoalHistoryPort goalHistoryPort;

    @Operation(
            summary = "Remover meta",
            description = "Exclui permanentemente uma meta. Apenas o dono pode remover.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Meta removida com sucesso")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        deleteGoalUseCase.execute(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Criar nova meta",
            description = "Define um objetivo financeiro com valor alvo e data limite.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Meta criada com sucesso")
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
                    @ApiResponse(responseCode = "200", description = "Meta atualizada com sucesso")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid CreateGoalRequest request,
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
            description = "Adiciona um valor ao saldo atual de uma meta específica e registra no histórico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso")
            }
    )
    @PostMapping("/{id}/deposit")
    public ResponseEntity<GoalDeposit> deposit(
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

    @Operation(summary = "Ver histórico da meta", description = "Lista todos os depósitos realizados em uma meta específica.")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<GoalDeposit>> getHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(goalHistoryPort.findByGoalId(id));
    }

    @Operation(summary = "Listar minhas metas", description = "Retorna todas as metas do usuário logado.")
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
