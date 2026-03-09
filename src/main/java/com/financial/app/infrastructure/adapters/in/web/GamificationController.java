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
            summary = "Minhas Medalhas (Achievements)",
            description = """
                Retorna todas as medalhas desbloqueadas pelo usuário.
                **IMPORTANTE (Frontend):** O backend detecta e salva as medalhas automaticamente com base em ações do usuário. NÃO é necessário enviar POST para desbloquear.
                
                **Gatilhos de Desbloqueio (Business Logic):**
                - **FIRST_STEPS**: Desbloqueia na 1ª transação (despesa/receita) cadastrada.
                - **GOAL_SETTER**: Desbloqueia ao criar a 1ª meta de economia.
                - **STREAK_7**: Desbloqueia ao atingir sequência de 7 dias de atividade.
                - **STREAK_30**: Desbloqueia ao atingir sequência de 30 dias de atividade.
                - **BUDGET_MASTER**: Desbloqueia mensalmente se nenhum orçamento for estourado.
                - **ELITE_SAVER**: Desbloqueia quando o usuário atinge o Nível 10 de XP.
                
                **Sugestão de UX:** Monitore o endpoint de Notificações para exibir alertas de novas medalhas em tempo real.
                """,
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
            summary = "Missões Diárias (Daily Missions)",
            description = """
                Retorna as missões para hoje e o progresso do usuário.
                **IMPORTANTE (Frontend):** O progresso das missões é contabilizado automaticamente pelo backend.
                
                **Como funciona a contagem:**
                - **Lançamento do Dia (TRANSACTION_COUNT):** Backend incrementa ao receber `POST /api/transactions`.
                - **Meta do Bem (GOAL_DEPOSIT):** Backend incrementa ao receber `POST /api/goals/{id}/deposit`.
                
                Ao atingir o `requiredCount`, o backend marca a missão como completada, concede o XP e gera uma notificação.
                """,
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
