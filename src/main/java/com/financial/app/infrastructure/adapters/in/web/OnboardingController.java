package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.CompleteOnboardingUseCase;
import com.financial.app.application.ports.in.command.OnboardingCommand;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "Fluxo inicial de configuração da conta")
public class OnboardingController {

    private final CompleteOnboardingUseCase completeOnboardingUseCase;

    @Operation(
        summary = "Finalizar Onboarding (Batch)",
        description = "Salva em uma única operação a renda mensal, despesas recorrentes e a meta principal do usuário.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Onboarding finalizado com sucesso")
        }
    )
    @PostMapping("/complete")
    public ResponseEntity<Void> completeOnboarding(
            @RequestBody 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(value = "{\"monthlyIncome\": 5000, \"fixedExpenses\": [{\"name\": \"Aluguel\", \"amount\": 1500, \"category\": \"HOUSING\"}], \"mainGoal\": {\"title\": \"Reserva\", \"targetAmount\": 10000, \"deadline\": \"2026-12-31T00:00:00\"}}"))
            )
            @Valid OnboardingRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        OnboardingCommand command = new OnboardingCommand(
                userId,
                request.monthlyIncome(),
                request.fixedExpenses(),
                request.mainGoal()
        );

        completeOnboardingUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
