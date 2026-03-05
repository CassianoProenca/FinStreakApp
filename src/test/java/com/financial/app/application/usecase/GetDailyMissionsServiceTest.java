package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.DailyMissionResult;
import com.financial.app.application.ports.out.*;
import com.financial.app.domain.model.DailyMission;
import com.financial.app.domain.model.GamificationProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetDailyMissionsServiceTest {

    @Mock private LoadDailyMissionsPort loadDailyMissionsPort;
    @Mock private LoadUserMissionCompletedPort loadUserMissionCompletedPort;
    @Mock private SaveUserMissionCompletedPort saveUserMissionCompletedPort;
    @Mock private LoadTransactionPort loadTransactionPort;
    @Mock private LoadGamificationProfilePort loadGamificationProfilePort;
    @Mock private SaveGamificationProfilePort saveGamificationProfilePort;
    @Mock private NotificationPort notificationPort;

    @InjectMocks
    private GetDailyMissionsService service;

    private UUID userId;
    private DailyMission transactionMission;
    private DailyMission goalMission;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        transactionMission = DailyMission.builder()
                .id(UUID.randomUUID())
                .title("Lançamento do Dia")
                .description("Registre qualquer despesa hoje")
                .xpReward(20)
                .missionType("TRANSACTION_COUNT")
                .requiredCount(1)
                .build();

        goalMission = DailyMission.builder()
                .id(UUID.randomUUID())
                .title("Meta do Bem")
                .description("Faça um depósito em qualquer meta")
                .xpReward(50)
                .missionType("GOAL_DEPOSIT")
                .requiredCount(1)
                .build();
    }

    @Test
    @DisplayName("Should return all missions with correct status when not started")
    void shouldReturnMissionsNotCompleted() {
        when(loadDailyMissionsPort.loadAll()).thenReturn(List.of(transactionMission, goalMission));
        when(loadUserMissionCompletedPort.wasCompletedToday(any(), any(), any())).thenReturn(false);
        when(loadTransactionPort.loadAllByQuery(any())).thenReturn(List.of()); // no transactions

        List<DailyMissionResult> results = service.execute(userId);

        assertEquals(2, results.size());
        assertFalse(results.get(0).completed());
        assertFalse(results.get(1).completed());
        assertEquals(0, results.get(0).currentCount());
    }

    @Test
    @DisplayName("Should mark mission as completed when progress meets requiredCount")
    void shouldAutoCompleteMissionWhenProgressMet() {
        com.financial.app.domain.model.Transaction mockTx =
                com.financial.app.domain.model.Transaction.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .amount(java.math.BigDecimal.TEN)
                        .type(com.financial.app.domain.model.enums.TransactionType.EXPENSE)
                        .build();

        when(loadDailyMissionsPort.loadAll()).thenReturn(List.of(transactionMission));
        when(loadUserMissionCompletedPort.wasCompletedToday(any(), any(), any())).thenReturn(false);
        when(loadTransactionPort.loadAllByQuery(any())).thenReturn(List.of(mockTx)); // 1 transaction

        GamificationProfile profile = GamificationProfile.builder()
                .userId(userId).totalXp(0L).currentStreak(1).maxStreak(1)
                .lastActivityDate(LocalDate.now()).build();
        when(loadGamificationProfilePort.loadByUserId(userId)).thenReturn(Optional.of(profile));
        when(saveGamificationProfilePort.save(any())).thenAnswer(i -> i.getArgument(0));

        List<DailyMissionResult> results = service.execute(userId);

        assertEquals(1, results.size());
        assertTrue(results.get(0).completed());
        verify(saveUserMissionCompletedPort).save(eq(userId), eq(transactionMission.getId()), any(LocalDate.class));
        verify(notificationPort).notifyUser(eq(userId), contains("Lançamento do Dia"), any());
    }

    @Test
    @DisplayName("Should not re-complete already completed mission")
    void shouldNotRecompleteMissionAlreadyCompleted() {
        when(loadDailyMissionsPort.loadAll()).thenReturn(List.of(transactionMission));
        when(loadUserMissionCompletedPort.wasCompletedToday(any(), any(), any())).thenReturn(true);
        // Even with progress met, already completed
        when(loadTransactionPort.loadAllByQuery(any())).thenReturn(List.of(
                com.financial.app.domain.model.Transaction.builder()
                        .id(UUID.randomUUID()).userId(userId)
                        .amount(java.math.BigDecimal.TEN)
                        .type(com.financial.app.domain.model.enums.TransactionType.EXPENSE)
                        .build()
        ));

        List<DailyMissionResult> results = service.execute(userId);

        assertTrue(results.get(0).completed());
        verify(saveUserMissionCompletedPort, never()).save(any(), any(), any());
    }

    @Test
    @DisplayName("Should cap currentCount at requiredCount in response")
    void shouldCapCurrentCountAtRequired() {
        List<com.financial.app.domain.model.Transaction> twoDespesas = List.of(
                com.financial.app.domain.model.Transaction.builder().id(UUID.randomUUID()).userId(userId)
                        .amount(java.math.BigDecimal.TEN)
                        .type(com.financial.app.domain.model.enums.TransactionType.EXPENSE).build(),
                com.financial.app.domain.model.Transaction.builder().id(UUID.randomUUID()).userId(userId)
                        .amount(java.math.BigDecimal.ONE)
                        .type(com.financial.app.domain.model.enums.TransactionType.EXPENSE).build()
        );

        when(loadDailyMissionsPort.loadAll()).thenReturn(List.of(transactionMission)); // requiredCount=1
        when(loadUserMissionCompletedPort.wasCompletedToday(any(), any(), any())).thenReturn(false);
        when(loadTransactionPort.loadAllByQuery(any())).thenReturn(twoDespesas); // 2 transactions > 1 required

        GamificationProfile profile = GamificationProfile.builder()
                .userId(userId).totalXp(0L).currentStreak(1).maxStreak(1)
                .lastActivityDate(LocalDate.now()).build();
        when(loadGamificationProfilePort.loadByUserId(userId)).thenReturn(Optional.of(profile));
        when(saveGamificationProfilePort.save(any())).thenAnswer(i -> i.getArgument(0));

        List<DailyMissionResult> results = service.execute(userId);

        // currentCount should be capped at requiredCount=1, not 2
        assertEquals(1, results.get(0).currentCount());
        assertEquals(1, results.get(0).requiredCount());
    }
}
