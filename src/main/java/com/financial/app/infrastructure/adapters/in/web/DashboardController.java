package com.financial.app.infrastructure.adapters.in.web;

import com.financial.app.application.ports.in.GetDashboardSummaryUseCase;
import com.financial.app.infrastructure.adapters.in.web.dto.response.DashboardSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Resumos financeiros consolidados")
public class DashboardController {

    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    @Operation(
        summary = "Resumo Mensal (Home)",
        description = "Retorna saldo, total de receitas/despesas, gastos por categoria, orçamentos e medalhas do mês.",
        parameters = {
            @Parameter(name = "month", description = "Mês de consulta (1-12)", example = "2"),
            @Parameter(name = "year", description = "Ano de consulta", example = "2026")
        }
    )
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        
        int queryMonth = (month != null) ? month : LocalDate.now().getMonthValue();
        int queryYear = (year != null) ? year : LocalDate.now().getYear();

        DashboardSummaryResponse summary = getDashboardSummaryUseCase.execute(userId, queryMonth, queryYear);
        return ResponseEntity.ok(summary);
    }
}
