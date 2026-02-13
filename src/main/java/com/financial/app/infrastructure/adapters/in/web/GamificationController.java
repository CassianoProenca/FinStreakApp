package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.GetGamificationProfileUseCase;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.in.web.dto.response.GamificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@Tag(name = "Gamificação", description = "Dados de perfil, XP, níveis e medalhas")
public class GamificationController {

    private final GetGamificationProfileUseCase getGamificationProfileUseCase;
    private final LoadUserPort loadUserPort;

    @Operation(summary = "Meu Perfil Gamificado", description = "Retorna XP, Nível, Streak e Avatar do usuário logado.")
    @GetMapping("/me")
    public ResponseEntity<GamificationResponse> getProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        GamificationProfile profile = getGamificationProfileUseCase.execute(userId);
        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(new GamificationResponse(
                profile.getUserId(),
                user.getName(),
                user.getAvatarUrl(),
                profile.getCurrentStreak(),
                profile.getMaxStreak(),
                profile.getTotalXp(),
                profile.getLevel(),
                profile.getXpForNextLevel(),
                profile.getLastActivityDate()
        ));
    }
}
