package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.GetDailyMissionsUseCase;
import com.financial.app.application.ports.in.GetGamificationProfileUseCase;
import com.financial.app.application.ports.in.GetUnlockedAvatarsUseCase;
import com.financial.app.application.ports.out.LoadAchievementsPort;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.in.web.dto.response.AchievementResponse;
import com.financial.app.infrastructure.adapters.in.web.dto.response.DailyMissionResponse;
import com.financial.app.infrastructure.adapters.in.web.dto.response.GamificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Gamificação", description = "Dados de perfil, XP, níveis, medalhas e missões diárias")
public class GamificationController {

    private final GetGamificationProfileUseCase getGamificationProfileUseCase;
    private final LoadUserPort loadUserPort;
    private final LoadAchievementsPort loadAchievementsPort;
    private final GetDailyMissionsUseCase getDailyMissionsUseCase;
    private final GetUnlockedAvatarsUseCase getUnlockedAvatarsUseCase;

    @Operation(
            summary = "Meu Perfil Gamificado",
            description = "Retorna o perfil de gamificação completo: nível, XP total, XP dentro do nível atual (para barra de progresso), streak atual, maior streak e data da última atividade.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso",
                            content = @Content(schema = @Schema(implementation = GamificationResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "404", description = "Perfil de gamificação não encontrado")
            }
    )
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

    @Operation(
            summary = "Minhas Medalhas",
            description = "Retorna todas as conquistas desbloqueadas pelo usuário. Tipos: FIRST_STEPS, STREAK_7, STREAK_30, ELITE_SAVER.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de conquistas retornada com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @GetMapping("/achievements/me")
    public ResponseEntity<List<AchievementResponse>> getAchievements(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<AchievementResponse> achievements = loadAchievementsPort.loadByUserId(userId).stream()
                .map(a -> new AchievementResponse(a.getId(), a.getType(), a.getName(), a.getDescription(), a.getEarnedAt()))
                .toList();
        return ResponseEntity.ok(achievements);
    }

    @Operation(
            summary = "Missões Diárias",
            description = "Retorna as missões disponíveis para hoje e o progresso atual do usuário. Missões completadas geram XP automaticamente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Missões retornadas com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @GetMapping("/missions/daily")
    public ResponseEntity<List<DailyMissionResponse>> getDailyMissions(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<DailyMissionResponse> responses = getDailyMissionsUseCase.execute(userId).stream()
                .map(r -> new DailyMissionResponse(
                        r.id(), r.title(), r.description(), r.xpReward(),
                        r.currentCount(), r.requiredCount(), r.completed()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Avatares Desbloqueados",
            description = "Retorna a lista de chaves de avatares que o usuário já conquistou. Retorna [\"default_avatar\"] se nenhum foi desbloqueado ainda.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de chaves de avatares"),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    @GetMapping("/avatars/unlocked")
    public ResponseEntity<List<String>> getUnlockedAvatars(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(getUnlockedAvatarsUseCase.execute(userId));
    }
}
