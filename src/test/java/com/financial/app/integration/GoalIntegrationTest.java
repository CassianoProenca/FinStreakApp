package com.financial.app.integration;

import com.fasterxml.jackson.databind.JsonNode;
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

        mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Travel to Japan"));

        mockMvc.perform(get("/api/goals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Travel to Japan"));
    }

    @Test
    @DisplayName("GET /api/goals/{id} should return goal by id for owner")
    void shouldGetGoalById() throws Exception {
        String token = getAccessTokenForUser("goal-getbyid@example.com", "pass123");

        CreateGoalRequest request = new CreateGoalRequest(
                "Emergency Fund",
                new BigDecimal("5000.00"),
                BigDecimal.ZERO,
                LocalDateTime.now().plusMonths(6),
                "shield"
        );

        var createResult = mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String goalId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/goals/" + goalId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goalId))
                .andExpect(jsonPath("$.title").value("Emergency Fund"));
    }

    @Test
    @DisplayName("GET /api/goals/{id} should return 404 for non-existent goal")
    void shouldReturn404ForNonExistentGoal() throws Exception {
        String token = getAccessTokenForUser("goal-404@example.com", "pass123");

        mockMvc.perform(get("/api/goals/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/goals/{id}/history should return 403 (IDOR protection) for another user's goal")
    void shouldReturn403ForAnotherUsersGoalHistory() throws Exception {
        // User A creates a goal
        String tokenA = getAccessTokenForUser("goal-owner@example.com", "pass123");
        CreateGoalRequest request = new CreateGoalRequest(
                "Private Goal",
                new BigDecimal("1000.00"),
                BigDecimal.ZERO,
                LocalDateTime.now().plusMonths(3),
                "lock"
        );

        var createResult = mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String goalId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // User B tries to access User A's goal history → should get 403
        String tokenB = getAccessTokenForUser("goal-attacker@example.com", "pass123");
        mockMvc.perform(get("/api/goals/" + goalId + "/history")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail when creating goal with past date")
    void shouldFailWithPastDate() throws Exception {
        String token = getAccessTokenForUser("failuser@example.com", "pass123");

        CreateGoalRequest request = new CreateGoalRequest(
                "Impossible Goal",
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                LocalDateTime.now().minusDays(1),
                "error"
        );

        mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
