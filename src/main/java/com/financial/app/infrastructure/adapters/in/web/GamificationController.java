package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.GetGamificationProfileUseCase;
import com.financial.app.domain.model.GamificationProfile;
import com.financial.app.infrastructure.adapters.in.web.dto.response.GamificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GetGamificationProfileUseCase getGamificationProfileUseCase;

    @GetMapping("/me")
    public ResponseEntity<GamificationResponse> getProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        GamificationProfile profile = getGamificationProfileUseCase.execute(userId);

        return ResponseEntity.ok(new GamificationResponse(
                profile.getUserId(),
                profile.getCurrentStreak(),
                profile.getMaxStreak(),
                profile.getTotalXp(),
                profile.getLastActivityDate()
        ));
    }
}
