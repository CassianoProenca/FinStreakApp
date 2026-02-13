package com.financial.app.integration;

import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateGoalRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GoalIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should create and list goals successfully")
    void shouldCreateAndListGoals() throws Exception {
        String token = getAccessTokenForUser("goaluser@example.com", "pass123");

        CreateGoalRequest request = new CreateGoalRequest(
                "Travel to Japan",
                new BigDecimal("15000.00"),
                BigDecimal.ZERO,
                LocalDateTime.now().plusMonths(12),
                "airplane"
        );

        // 1. Create Goal
        mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Travel to Japan"));

        // 2. List Goals
        mockMvc.perform(get("/api/goals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Travel to Japan"));
    }

    @Test
    @DisplayName("Should fail when creating goal with past date")
    void shouldFailWithPastDate() throws Exception {
        String token = getAccessTokenForUser("failuser@example.com", "pass123");

        CreateGoalRequest request = new CreateGoalRequest(
                "Impossible Goal",
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                LocalDateTime.now().minusDays(1), // Past date
                "error"
        );

        mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
