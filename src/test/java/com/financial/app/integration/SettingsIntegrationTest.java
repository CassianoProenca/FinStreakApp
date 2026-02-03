package com.financial.app.integration;

import com.financial.app.domain.model.enums.Theme;
import com.financial.app.infrastructure.adapters.in.web.dto.request.UpdateSettingsRequest;
import com.financial.app.infrastructure.adapters.out.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SettingsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("Should update user settings and persist changes")
    void shouldUpdateSettings() throws Exception {
        String email = "settings@example.com";
        String token = getAccessTokenForUser(email, "pass123");

        UpdateSettingsRequest request = new UpdateSettingsRequest(
                Theme.DARK,
                false // Disable notifications
        );

        // 1. Update Settings
        mockMvc.perform(patch("/api/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        // 2. Verify Persistence directly in DB
        var userEntity = userJpaRepository.findByEmail(email).orElseThrow();
        assertEquals(Theme.DARK, userEntity.getPreferences().getTheme());
        assertEquals(false, userEntity.getPreferences().isNotificationsEnabled());
    }
}
