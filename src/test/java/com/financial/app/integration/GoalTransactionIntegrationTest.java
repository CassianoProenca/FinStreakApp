package com.financial.app.integration;

import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateGoalRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.GoalDepositRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GoalTransactionIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should create properly formatted transaction on goal deposit and withdrawal")
    void shouldCreateFormattedTransactionOnDepositAndWithdrawal() throws Exception {
        String token = getAccessTokenForUser("goal-trans@example.com", "pass123");

        // 1. Create a Goal
        CreateGoalRequest createRequest = new CreateGoalRequest(
                "New House",
                new BigDecimal("50000.00"),
                BigDecimal.ZERO,
                LocalDateTime.now().plusYears(5),
                "home"
        );

        MvcResult createResult = mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode goalNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String goalId = goalNode.get("id").asText();

        // 2. Perform a Deposit
        GoalDepositRequest depositRequest = new GoalDepositRequest(new BigDecimal("1000.00"), "Saving bonus");
        mockMvc.perform(post("/api/goals/" + goalId + "/deposit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk());

        // 3. Verify Transaction in Extrato
        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Aporte - New House"))
                .andExpect(jsonPath("$.content[0].iconKey").value("home"))
                .andExpect(jsonPath("$.content[0].goalId").value(goalId));

        // 4. Perform a Withdrawal
        GoalDepositRequest withdrawRequest = new GoalDepositRequest(new BigDecimal("200.00"), "Emergency");
        mockMvc.perform(post("/api/goals/" + goalId + "/withdraw")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk());

        // 5. Verify Withdrawal Transaction
        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Resgate - New House"))
                .andExpect(jsonPath("$.content[0].iconKey").value("home"))
                .andExpect(jsonPath("$.content[0].goalId").value(goalId));
    }
}
