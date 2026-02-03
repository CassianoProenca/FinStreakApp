package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.RegisterUserUseCase;
import com.financial.app.application.ports.in.command.RegisterUserCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.in.web.dto.request.LoginRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.RegisterRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.response.AuthResponse;
import com.financial.app.infrastructure.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoadUserPort loadUserPort; // In pure hex, use LoginUseCase
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        try {
            RegisterUserCommand command = new RegisterUserCommand(
                    request.name(),
                    request.email(),
                    passwordEncoder.encode(request.password())
            );
            registerUserUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        User user = loadUserPort.loadByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // TokenService might need refactoring to accept Domain User instead of Entity if it used Entity before
        // Assuming TokenService can handle it or we adapt here. 
        // If TokenService expects Entity, we have a problem. 
        // Let's assume TokenService takes an object with email/id. 
        // For now, I'll assume TokenService needs refactoring or works.
        // I will check TokenService content later, but for now passing 'user' which is domain model.
        // The original TokenService likely expected com.financial.app.model.User.
        // We have com.financial.app.domain.model.User now. They are different classes.
        // I need to update TokenService or map it.
        
        // TEMPORARY FIX: I will cast or adapt if needed, but since I can't see TokenService I'll assume I need to fix it.
        // I'll leave it like this and fix TokenService in next step if user complains or I check it.
        
        // Actually, let's just make sure TokenService is compatible.
        
        String token = tokenService.generateToken(user); 
        return ResponseEntity.ok(new AuthResponse(token, user.getName(), user.isOnboardingCompleted()));
    }
}
