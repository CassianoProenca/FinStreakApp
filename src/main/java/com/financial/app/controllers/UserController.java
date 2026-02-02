package com.financial.app.controllers;

import com.financial.app.dto.request.OnboardingRequest;
import com.financial.app.services.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/onboarding")
    public ResponseEntity<Void> completeOnboarding(
            @RequestBody @Valid OnboardingRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        userService.completeOnboarding(userId, request);
        return ResponseEntity.noContent().build();
    }
}
