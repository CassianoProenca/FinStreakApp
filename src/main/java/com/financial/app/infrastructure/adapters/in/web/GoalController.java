package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.CreateGoalUseCase;
import com.financial.app.application.ports.in.ListGoalsUseCase;
import com.financial.app.application.ports.in.command.CreateGoalCommand;
import com.financial.app.domain.model.Goal;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateGoalRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.response.GoalResponse;
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
public class GoalController {

    private final CreateGoalUseCase createGoalUseCase;
    private final ListGoalsUseCase listGoalsUseCase;

    @PostMapping
    public ResponseEntity<GoalResponse> create(
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
                request.icon()
        );

        Goal goal = createGoalUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(goal));
    }

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
                goal.getIcon()
        );
    }
}
