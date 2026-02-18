package com.financial.app.integration;

import com.financial.app.infrastructure.adapters.in.web.dto.request.ChangePasswordRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.ForgotPasswordRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.ResetPasswordRequest;
import com.financial.app.infrastructure.adapters.out.persistence.repository.PasswordResetTokenJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PasswordResetTokenJpaRepository passwordResetTokenRepository;

    // ── change-password ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should change password successfully with correct old password")
    void shouldChangePasswordSuccessfully() throws Exception {
        String email = "changepass@example.com";
        String oldPassword = "oldPass123";
        String token = getAccessTokenForUser(email, oldPassword);

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, "newPass456");

        mockMvc.perform(post("/api/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 400 when old password is incorrect")
    void shouldReturn400WhenOldPasswordWrong() throws Exception {
        String email = "changepass-wrong@example.com";
        String token = getAccessTokenForUser(email, "correctPass123");

        ChangePasswordRequest request = new ChangePasswordRequest("wrongOldPass", "newPass456");

        mockMvc.perform(post("/api/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 401 when change-password is called without token")
    void shouldReturn401WhenChangePasswordWithoutToken() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("old", "newPass456");

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ── forgot-password ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return 200 for existing email on forgot-password")
    void shouldReturn200ForForgotPasswordWithExistingEmail() throws Exception {
        String email = "forgot@example.com";
        getAccessTokenForUser(email, "somePass123"); // ensure user exists

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 200 even for non-existing email (no information leak)")
    void shouldReturn200ForForgotPasswordWithUnknownEmail() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest("nobody@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 when forgot-password body is invalid")
    void shouldReturn400ForInvalidForgotPasswordBody() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"not-an-email\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── reset-password ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should reset password successfully with a valid token")
    void shouldResetPasswordWithValidToken() throws Exception {
        String email = "resetpass@example.com";
        getAccessTokenForUser(email, "oldPass123");

        // Trigger token generation
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(email))))
                .andExpect(status().isOk());

        // Retrieve token from DB (LogEmailAdapter writes it to log; entity is persisted)
        String rawToken = passwordResetTokenRepository.findAll().stream()
                .filter(t -> t.getUserId() != null && !t.isUsed())
                .map(t -> t.getToken())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No password reset token found in DB"));

        ResetPasswordRequest request = new ResetPasswordRequest(rawToken, "brandNewPass789");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        // Token should now be marked as used
        assertTrue(passwordResetTokenRepository.findByToken(rawToken)
                .map(t -> t.isUsed())
                .orElse(false));
    }

    @Test
    @DisplayName("Should return 400 when reset-password token is invalid")
    void shouldReturn400ForInvalidResetToken() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("invalid-token-xyz", "newPass123");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when reset-password token is reused")
    void shouldReturn400WhenTokenReused() throws Exception {
        String email = "reuse-token@example.com";
        getAccessTokenForUser(email, "pass123");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(email))))
                .andExpect(status().isOk());

        String rawToken = passwordResetTokenRepository.findAll().stream()
                .filter(t -> !t.isUsed())
                .map(t -> t.getToken())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No token found"));

        ResetPasswordRequest request = new ResetPasswordRequest(rawToken, "firstNewPass");

        // First use succeeds
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        // Second use fails
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
