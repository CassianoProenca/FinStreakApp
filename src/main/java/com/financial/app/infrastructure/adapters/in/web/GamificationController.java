package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.GetGamificationProfileUseCase;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.in.web.dto.response.AchievementResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@Tag(name = "Gamificação", description = "Dados de perfil, XP, níveis e medalhas")
public class GamificationController {

    private final GetGamificationProfileUseCase getGamificationProfileUseCase;
    private final LoadUserPort loadUserPort;
    private final LoadAchievementsPort loadAchievementsPort;

    @Operation(summary = "Meu Perfil Gamificado", description = "Retorna XP, Nível, Streak e Avatar do usuário logado.")
    @GetMapping("/me")
    public ResponseEntity<GamificationResponse> getProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        GamificationProfile profile = getGamificationProfileUseCase.execute(userId);
        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        long xpWithinCurrentLevel = profile.getTotalXp() - ((long) (profile.getLevel() - 1) * 500L);

        return ResponseEntity.ok(new GamificationResponse(
                profile.getUserId(),
                user.getName(),
                user.getAvatarUrl(),
                profile.getCurrentStreak(),
                profile.getMaxStreak(),
                profile.getTotalXp(),
                profile.getLevel(),
                profile.getXpForNextLevel(),
                xpWithinCurrentLevel,
                profile.getLastActivityDate()
        ));
    }

    @Operation(summary = "Minhas Medalhas", description = "Retorna todas as conquistas do usuário logado.")
    @GetMapping("/achievements/me")
    public ResponseEntity<List<AchievementResponse>> getAchievements(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<AchievementResponse> achievements = loadAchievementsPort.loadByUserId(userId).stream()
                .map(a -> new AchievementResponse(a.getId(), a.getType(), a.getName(), a.getDescription(), a.getEarnedAt()))
                .toList();
        return ResponseEntity.ok(achievements);
    }
}
