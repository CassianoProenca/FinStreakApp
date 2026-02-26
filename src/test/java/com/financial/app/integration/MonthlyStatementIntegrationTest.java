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
class MonthlyStatementIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should return correct opening and closing balances")
    void shouldReturnCorrectBalances() throws Exception {
        String email = "statement-test@example.com";
        String token = getAccessTokenForUser(email, "pass123");

        // Prior month income: January 2026
        createTransaction(token, new BigDecimal("1000"), "Salário Jan", TransactionType.INCOME,
                TransactionCategory.SALARY, LocalDateTime.of(2026, 1, 15, 10, 0));

        // Current month (February 2026)
        createTransaction(token, new BigDecimal("5000"), "Salário Fev", TransactionType.INCOME,
                TransactionCategory.SALARY, LocalDateTime.of(2026, 2, 5, 10, 0));
        createTransaction(token, new BigDecimal("1500"), "Aluguel", TransactionType.EXPENSE,
                TransactionCategory.HOUSING, LocalDateTime.of(2026, 2, 10, 10, 0));
        createTransaction(token, new BigDecimal("300"), "Supermercado", TransactionType.EXPENSE,
                TransactionCategory.FOOD, LocalDateTime.of(2026, 2, 15, 10, 0));

        var result = mockMvc.perform(get("/api/transactions/statement?month=2&year=2026")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());

        assertEquals(2, body.get("month").asInt());
        assertEquals(2026, body.get("year").asInt());
        // opening = 1000 (Jan income)
        assertEquals(1000.0, body.get("openingBalance").asDouble(), 0.01);
        assertEquals(5000.0, body.get("totalIncome").asDouble(), 0.01);
        assertEquals(1800.0, body.get("totalExpenses").asDouble(), 0.01);
        assertEquals(0.0, body.get("totalAllocations").asDouble(), 0.01);
        assertEquals(0.0, body.get("totalWithdrawals").asDouble(), 0.01);
        // closing = 1000 + 5000 - 1800 = 4200
        assertEquals(4200.0, body.get("closingBalance").asDouble(), 0.01);

        // spendingByCategory should have HOUSING and FOOD
        assertTrue(body.get("spendingByCategory").has("HOUSING"));
        assertTrue(body.get("spendingByCategory").has("FOOD"));

        // transactions list should have 3 transactions for February
        assertEquals(3, body.get("transactions").size());
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    void shouldReturn401WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/transactions/statement?month=2&year=2026"))
                .andExpect(status().isUnauthorized());
    }

    private void createTransaction(String token, BigDecimal amount, String description,
                                   TransactionType type, TransactionCategory category,
                                   LocalDateTime date) throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                amount, description, type, category, date, false, null, null, null, null
        );
        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
