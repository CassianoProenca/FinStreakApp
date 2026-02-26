package com.financial.app.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateTransactionRequest;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InstallmentIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Creating 3 installments should save 3 transaction records")
    void shouldCreate3RecordsWhenInstallmentsIs3() throws Exception {
        String email = "installment-test@example.com";
        String token = getAccessTokenForUser(email, "pass123");

        LocalDateTime now = LocalDateTime.now().withNano(0);

        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("150.00"), "Notebook parcelado", TransactionType.EXPENSE,
                TransactionCategory.SHOPPING, now, false, null, null, null, 3
        );

        var createResult = mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode parent = objectMapper.readTree(createResult.getResponse().getContentAsString());
        assertEquals(1, parent.get("currentInstallment").asInt());
        assertEquals(3, parent.get("totalInstallments").asInt());

        // upcoming should show installments 2 and 3
        var upcomingResult = mockMvc.perform(get("/api/transactions/upcoming")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode upcoming = objectMapper.readTree(upcomingResult.getResponse().getContentAsString());
        long installmentCount = 0;
        for (JsonNode item : upcoming) {
            if (!item.get("isProjection").asBoolean() && item.has("totalInstallments")
                    && item.get("totalInstallments").asInt() == 3) {
                installmentCount++;
            }
        }
        assertEquals(2, installmentCount, "Should have 2 future installments (2nd and 3rd)");
    }

    @Test
    @DisplayName("Single transaction (no installments) should not appear in upcoming as installment")
    void singleTransactionShouldNotAppearInUpcoming() throws Exception {
        String email = "no-installment@example.com";
        String token = getAccessTokenForUser(email, "pass123");

        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("50.00"), "Café", TransactionType.EXPENSE,
                TransactionCategory.FOOD, LocalDateTime.now(), false, null, null, null, null
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        var upcomingResult = mockMvc.perform(get("/api/transactions/upcoming")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode upcoming = objectMapper.readTree(upcomingResult.getResponse().getContentAsString());
        long installmentItems = 0;
        for (JsonNode item : upcoming) {
            if (!item.get("isProjection").asBoolean()) {
                installmentItems++;
            }
        }
        assertEquals(0, installmentItems);
    }

    @Test
    @DisplayName("Should return 401 without authentication on /upcoming")
    void shouldReturn401WithoutAuthOnUpcoming() throws Exception {
        mockMvc.perform(get("/api/transactions/upcoming"))
                .andExpect(status().isUnauthorized());
    }
}
