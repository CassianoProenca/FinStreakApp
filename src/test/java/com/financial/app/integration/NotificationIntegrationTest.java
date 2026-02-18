package com.financial.app.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateTransactionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /api/notifications should return empty list before any activity")
    void shouldReturnEmptyListInitially() throws Exception {
        String token = getAccessTokenForUser("notif-empty@example.com", "pass123");

        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/notifications should return notifications after a transaction")
    void shouldReturnNotificationsAfterTransaction() throws Exception {
        String token = getAccessTokenForUser("notif-active@example.com", "pass123");

        // Trigger gamification (creates streak + FIRST_STEPS notification)
        CreateTransactionRequest txRequest = new CreateTransactionRequest(
                new BigDecimal("100"),
                "Salário",
                TransactionType.INCOME,
                TransactionCategory.SALARY,
                LocalDateTime.now(),
                false, null, null, "money"
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(txRequest)))
                .andExpect(status().isCreated());

        // Notifications should now exist
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].message").exists())
                .andExpect(jsonPath("$[0].type").exists())
                .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    @DisplayName("PATCH /api/notifications/{id}/read should mark notification as read")
    void shouldMarkNotificationAsRead() throws Exception {
        String token = getAccessTokenForUser("notif-mark@example.com", "pass123");

        // Create a transaction to generate a notification
        CreateTransactionRequest txRequest = new CreateTransactionRequest(
                new BigDecimal("50"),
                "Freelance",
                TransactionType.INCOME,
                TransactionCategory.OTHER,
                LocalDateTime.now(),
                false, null, null, "work"
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(txRequest)))
                .andExpect(status().isCreated());

        // Get notifications and grab the first ID
        var listResult = mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andReturn();

        JsonNode notifications = objectMapper.readTree(listResult.getResponse().getContentAsString());
        String notificationId = notifications.get(0).get("id").asText();

        // Mark as read
        mockMvc.perform(patch("/api/notifications/" + notificationId + "/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify it is now read
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + notificationId + "')].isRead").value(true));
    }

    @Test
    @DisplayName("GET /api/notifications should return 401 without token")
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }
}
