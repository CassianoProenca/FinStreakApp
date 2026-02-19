package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.ForgotPasswordUseCase;
import com.financial.app.application.ports.in.RegisterUserUseCase;
import com.financial.app.application.ports.in.ResetPasswordUseCase;
import com.financial.app.application.ports.in.command.RegisterUserCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.in.web.dto.request.ChangePasswordRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.ForgotPasswordRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.LoginRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.RegisterRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.ResetPasswordRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.response.AuthResponse;
import com.financial.app.infrastructure.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria uma conta no sistema e gera automaticamente um avatar padrão.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
                    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criação de conta",
                    content = @Content(examples = @ExampleObject(value = "{\"name\": \"Lucas Silva\", \"email\": \"lucas@example.com\", \"password\": \"senha123\"}"))
            )
            @Valid RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())
        );
        registerUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Realizar Login",
            description = "Autentica o usuário e retorna o token JWT junto com os dados de perfil.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais de acesso",
                    content = @Content(examples = @ExampleObject(value = "{\"email\": \"lucas@example.com\", \"password\": \"senha123\"}"))
            )
            @Valid LoginRequest request) {
        User user = loadUserPort.loadByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getName(),
                user.getAvatarUrl(),
                user.isOnboardingCompleted(),
                user.getMonthlyIncome()
        ));
    }

    @Operation(
            summary = "Alterar Senha",
            description = "Altera a senha do usuário autenticado. Exige a senha atual para confirmação.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Senha atual incorreta")
            }
    )
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        saveUserPort.save(user);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Esqueci minha senha",
            description = "Envia um token de redefinição por e-mail. Sempre retorna 200 (não revela se o e-mail existe).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Solicitação processada")
            }
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.execute(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Redefinir Senha",
            description = "Redefine a senha usando um token válido obtido via forgot-password.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
            }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.execute(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
