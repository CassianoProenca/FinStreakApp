package com.financial.app.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateTransactionRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.LoginRequest;
import com.financial.app.infrastructure.adapters.in.web.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Uses application-test.yaml (H2)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FullFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Static to share state between ordered tests (or could do one big test method)
    private static String jwtToken;
    private static final String EMAIL = "hero@example.com";
    private static final String PASSWORD = "securePassword123";

    @Test
    @Order(1)
    @DisplayName("1. Should Register a new User")
    void shouldRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest("Hero User", EMAIL, PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    @DisplayName("2. Should Login and retrieve JWT")
    void shouldLoginAndGetToken() throws Exception {
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        jwtToken = jsonNode.get("token").asText();
    }

    @Test
    @Order(3)
    @DisplayName("3. Should Create a Transaction (and trigger Gamification)")
    void shouldCreateTransaction() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("50.00"),
                "First Quest Reward",
                TransactionType.INCOME,
                TransactionCategory.SALARY, // Using Salary as "Quest Reward" generic
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    @Order(4)
    @DisplayName("4. Should Retrieve Gamification Profile and verify Streak")
    void shouldGetGamificationProfile() throws Exception {
        mockMvc.perform(get("/api/gamification/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andDo(print())
                .andExpect(status().isOk())
                // Assuming CheckStreakUseCase logic initializes profile if missing and adds 1 streak for today's activity
                .andExpect(jsonPath("$.currentStreak").value(1))
                .andExpect(jsonPath("$.maxStreak").value(1));
    }
}
