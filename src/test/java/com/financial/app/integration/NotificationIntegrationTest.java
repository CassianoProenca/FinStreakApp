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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
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
    @DisplayName("GET /api/notifications should return notifications with title field after a transaction")
    void shouldReturnNotificationsWithTitleAfterTransaction() throws Exception {
        String token = getAccessTokenForUser("notif-active@example.com", "pass123");

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

        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").value(notNullValue()))
                .andExpect(jsonPath("$[0].message").exists())
                .andExpect(jsonPath("$[0].type").exists())
                .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    @DisplayName("GET /api/notifications/unread-count should return correct count")
    void shouldReturnUnreadCount() throws Exception {
        String token = getAccessTokenForUser("notif-count@example.com", "pass123");

        // Before any transaction, unread count should be 0
        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(0));

        // Create transaction to generate notifications
        CreateTransactionRequest txRequest = new CreateTransactionRequest(
                new BigDecimal("50"),
                "Bônus",
                TransactionType.INCOME,
                TransactionCategory.OTHER,
                LocalDateTime.now(),
                false, null, null, "star"
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(txRequest)))
                .andExpect(status().isCreated());

        // Unread count should now be > 0
        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(greaterThan(0)));
    }

    @Test
    @DisplayName("PATCH /api/notifications/{id}/read should mark notification as read")
    void shouldMarkNotificationAsRead() throws Exception {
        String token = getAccessTokenForUser("notif-mark@example.com", "pass123");

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

        var listResult = mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)))
                .andReturn();

        JsonNode notifications = objectMapper.readTree(listResult.getResponse().getContentAsString());
        String notificationId = notifications.get(0).get("id").asText();

        mockMvc.perform(patch("/api/notifications/" + notificationId + "/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + notificationId + "')].isRead").value(true));
    }

    @Test
    @DisplayName("PATCH /api/notifications/{id}/read should return 403 for another user's notification")
    void shouldReturn403WhenMarkingAnotherUsersNotification() throws Exception {
        // User A creates a transaction → generates a notification
        String tokenA = getAccessTokenForUser("notif-owner@example.com", "pass123");
        CreateTransactionRequest txRequest = new CreateTransactionRequest(
                new BigDecimal("100"),
                "Income",
                TransactionType.INCOME,
                TransactionCategory.SALARY,
                LocalDateTime.now(),
                false, null, null, "money"
        );
        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(txRequest)))
                .andExpect(status().isCreated());

        var listResult = mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode notifications = objectMapper.readTree(listResult.getResponse().getContentAsString());
        String notificationId = notifications.get(0).get("id").asText();

        // User B tries to mark User A's notification as read → should get 403
        String tokenB = getAccessTokenForUser("notif-attacker@example.com", "pass123");
        mockMvc.perform(patch("/api/notifications/" + notificationId + "/read")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/notifications should return 401 without token")
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }
}
