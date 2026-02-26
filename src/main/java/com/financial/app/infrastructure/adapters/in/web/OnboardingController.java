package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.CompleteOnboardingUseCase;
import com.financial.app.application.ports.in.command.OnboardingCommand;
import com.financial.app.application.ports.in.command.OnboardingExpenseItem;
import com.financial.app.application.ports.in.command.OnboardingGoalItem;
import com.financial.app.infrastructure.adapters.in.web.dto.request.OnboardingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "Fluxo inicial de configuração da conta")
public class OnboardingController {

    private final CompleteOnboardingUseCase completeOnboardingUseCase;

    @Operation(
        summary = "Finalizar Onboarding (Batch)",
        description = "Salva em uma única operação atômica a renda mensal, a lista de despesas fixas recorrentes e a meta financeira principal.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Onboarding finalizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
        }
    )
    @PostMapping("/complete")
    public ResponseEntity<Void> completeOnboarding(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(value = "{\"monthlyIncome\": 5000, \"fixedExpenses\": [{\"name\": \"Aluguel\", \"amount\": 1500, \"category\": \"HOUSING\", \"iconKey\": \"house\"}], \"mainGoal\": {\"title\": \"Reserva de Emergência\", \"targetAmount\": 10000, \"deadline\": \"2026-12-31T00:00:00\", \"iconKey\": \"shield\"}}"))
            )
            @Valid OnboardingRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        // Map infra DTOs → domain value objects (#29)
        List<OnboardingExpenseItem> expenses = request.fixedExpenses() == null ? List.of() :
                request.fixedExpenses().stream()
                        .map(e -> new OnboardingExpenseItem(e.name(), e.amount(), e.category(), e.iconKey()))
                        .collect(Collectors.toList());

        OnboardingGoalItem goal = request.mainGoal() == null ? null :
                new OnboardingGoalItem(
                        request.mainGoal().title(),
                        request.mainGoal().targetAmount(),
                        request.mainGoal().deadline(),
                        request.mainGoal().iconKey()
                );

        OnboardingCommand command = new OnboardingCommand(userId, request.monthlyIncome(), expenses, goal);
        completeOnboardingUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}


