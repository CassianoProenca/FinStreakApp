package com.financial.app.controllers;

import com.financial.app.dto.response.GamificationResponse;
import com.financial.app.model.GamificationProfile;
import com.financial.app.repositories.GamificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.financial.app.dto.request.CheckInRequest;
import com.financial.app.events.CheckInEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/me")
    public ResponseEntity<GamificationResponse> getMyProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        // Busca o perfil no banco. Se não existir (usuário novo), retorna um zerado na memória.
        GamificationProfile profile = repository.findByUserId(userId)
                .orElse(GamificationProfile.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .maxStreak(0)
                        .totalXp(0L)
                        .build());

        GamificationResponse response = new GamificationResponse(
                profile.getUserId(),
                profile.getCurrentStreak(),
                profile.getMaxStreak(),
                profile.getTotalXp(),
                profile.getLastActivityDate()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkin")
    public ResponseEntity<Void> doDailyCheckIn(
            @RequestBody(required = false) CheckInRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        String note = (request != null) ? request.note() : "Dia sem gastos";

        eventPublisher.publishEvent(new CheckInEvent(userId, note));

        return ResponseEntity.ok().build();
    }
}