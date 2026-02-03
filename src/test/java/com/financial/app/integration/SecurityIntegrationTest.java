package com.financial.app.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should return 401 Unauthorized when accessing protected endpoint without token")
    void shouldDenyAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when accessing with invalid token")
    void shouldDenyAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer invalid_token_123"))
                .andExpect(status().isUnauthorized());
    }
}
