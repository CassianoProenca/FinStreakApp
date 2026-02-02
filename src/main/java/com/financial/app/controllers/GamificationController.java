package com.financial.app.controllers;

import com.financial.app.dto.response.GamificationResponse;
import com.financial.app.model.GamificationProfile;
import com.financial.app.repositories.GamificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationRepository repository;

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
}