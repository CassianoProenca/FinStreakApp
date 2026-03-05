package com.financial.app.integration;

import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import com.financial.app.infrastructure.adapters.in.web.dto.request.CreateTransactionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MonthlyStatementIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /api/transactions/statement should return 401 without token")
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/transactions/statement?month=1&year=2026"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/transactions/statement should return statement for current month")
    void shouldReturnStatementForCurrentMonth() throws Exception {
        String token = getAccessTokenForUser("statement-user@example.com", "pass123");
        YearMonth current = YearMonth.now();

        // Create an income transaction this month
        CreateTransactionRequest income = new CreateTransactionRequest(
                new BigDecimal("3000.00"),
                "Salário",
                TransactionType.INCOME,
                TransactionCategory.SALARY,
                LocalDateTime.now(),
                false, null, null, "money"
        );
        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(income)))
                .andExpect(status().isCreated());

        // Create an expense transaction this month
        CreateTransactionRequest expense = new CreateTransactionRequest(
                new BigDecimal("500.00"),
                "Supermercado",
                TransactionType.EXPENSE,
                TransactionCategory.FOOD,
                LocalDateTime.now(),
                false, null, null, "food"
        );
        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/transactions/statement")
                        .param("month", String.valueOf(current.getMonthValue()))
                        .param("year", String.valueOf(current.getYear()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(current.getMonthValue()))
                .andExpect(jsonPath("$.year").value(current.getYear()))
                .andExpect(jsonPath("$.totalIncome").value(greaterThan(0.0)))
                .andExpect(jsonPath("$.totalExpenses").value(greaterThan(0.0)))
                .andExpect(jsonPath("$.closingBalance").exists())
                .andExpect(jsonPath("$.openingBalance").exists())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.spendingByCategory").exists());
    }

    @Test
    @DisplayName("GET /api/transactions/statement should return 400 for future month")
    void shouldReturn400ForFutureMonth() throws Exception {
        String token = getAccessTokenForUser("statement-future@example.com", "pass123");
        YearMonth future = YearMonth.now().plusMonths(1);

        mockMvc.perform(get("/api/transactions/statement")
                        .param("month", String.valueOf(future.getMonthValue()))
                        .param("year", String.valueOf(future.getYear()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/transactions/statement should return 400 for month more than 3 months ago")
    void shouldReturn400ForTooOldMonth() throws Exception {
        String token = getAccessTokenForUser("statement-old@example.com", "pass123");
        YearMonth tooOld = YearMonth.now().minusMonths(4);

        mockMvc.perform(get("/api/transactions/statement")
                        .param("month", String.valueOf(tooOld.getMonthValue()))
                        .param("year", String.valueOf(tooOld.getYear()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/transactions/statement should return 400 for invalid month")
    void shouldReturn400ForInvalidMonth() throws Exception {
        String token = getAccessTokenForUser("statement-invalid@example.com", "pass123");

        mockMvc.perform(get("/api/transactions/statement")
                        .param("month", "13")
                        .param("year", "2026")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/transactions/statement should allow querying 3 months ago")
    void shouldAllowQueryingThreeMonthsAgo() throws Exception {
        String token = getAccessTokenForUser("statement-3months@example.com", "pass123");
        YearMonth threeMonthsAgo = YearMonth.now().minusMonths(3);

        mockMvc.perform(get("/api/transactions/statement")
                        .param("month", String.valueOf(threeMonthsAgo.getMonthValue()))
                        .param("year", String.valueOf(threeMonthsAgo.getYear()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(threeMonthsAgo.getMonthValue()))
                .andExpect(jsonPath("$.year").value(threeMonthsAgo.getYear()))
                .andExpect(jsonPath("$.transactions").isArray());
    }
}
