package com.financial.app.integration;

import com.financial.app.infrastructure.adapters.in.web.dto.request.LoginRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailNormalizationIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should normalize email to lowercase on registration and allow login with mixed case")
    void shouldNormalizeEmailAndAllowLoginWithMixedCase() throws Exception {
        String emailWithCaps = "Cassiano@Gmail.com";
        String emailLower = "cassiano@gmail.com";
        String password = "password123";

        // 1. Register with mixed case
        RegisterRequest registerRequest = new RegisterRequest("Cassiano", emailWithCaps, password);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // 2. Try to login with mixed case - should work
        LoginRequest loginRequestCaps = new LoginRequest(emailWithCaps, password);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestCaps)))
                .andExpect(status().isOk());

        // 3. Try to login with lowercase - should also work
        LoginRequest loginRequestLower = new LoginRequest(emailLower, password);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestLower)))
                .andExpect(status().isOk());

        // 4. Try to register same email with lowercase - should fail (400 Bad Request)
        // Note: RegisterUserService throws BusinessException "E-mail já cadastrado"
        // which GlobalExceptionHandler maps to 400
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("Other", emailLower, password))))
                .andExpect(status().isBadRequest());
    }
}
