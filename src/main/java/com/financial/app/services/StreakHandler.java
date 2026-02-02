package com.financial.app.services;

import com.financial.app.events.CheckInEvent;
import com.financial.app.events.TransactionCreatedEvent;
import com.financial.app.model.GamificationProfile;
import com.financial.app.repositories.GamificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreakHandler {

    private final GamificationRepository repository;

    private static final Long XP_TRANSACTION = 10L;
    private static final Long XP_CHECKIN = 20L;
    private static final Long XP_STREAK_BONUS = 50L;

    /**
     * OUVIDO 1: Escuta quando uma transação é criada (Gasto ou Ganho)
     */
    @EventListener
    @Transactional
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        log.info("🎮 Evento financeiro recebido! User: {}", event.userId());
        processActivity(event.userId(), event.date().toLocalDate(), XP_TRANSACTION);
    }

    /**
     * OUVIDO 2: Escuta quando o usuário faz Check-in de Economia (Botão "Não gastei nada")
     */
    @EventListener
    @Transactional
    public void handleCheckIn(CheckInEvent event) {
        log.info("🎮 Check-in de Economia recebido! User: {}", event.userId());
        log.info("📝 Nota do usuário: {}", event.note());

        processActivity(event.userId(), LocalDate.now(), XP_CHECKIN);
    }

    private void processActivity(UUID userId, LocalDate activityDate, Long xpEarned) {
        // 1. Busca ou Cria (Onboarding)
        GamificationProfile profile = repository.findByUserId(userId)
                .orElseGet(() -> createNewProfile(userId));

        LocalDate lastActivity = profile.getLastActivityDate();

        // 2. O Algoritmo do Tempo
        if (lastActivity == null) {
            // Primeira atividade da vida
            startNewStreak(profile, activityDate, xpEarned);
        } else if (lastActivity.isEqual(activityDate)) {
            // Já interagiu hoje? Apenas soma o XP da ação (sem bônus de dia)
            addXp(profile, xpEarned);
            log.info("⚠️ Atividade extra no mesmo dia. +{} XP.", xpEarned);
        } else if (lastActivity.plusDays(1).isEqual(activityDate)) {
            // Veio ontem e veio hoje? Aumenta o fogo! 🔥
            incrementStreak(profile, activityDate, xpEarned);
        } else {
            // Quebrou o streak (passou mais de 1 dia) 😢
            resetStreak(profile, activityDate, xpEarned);
        }

        repository.save(profile);
    }

    private GamificationProfile createNewProfile(UUID userId) {
        return GamificationProfile.builder()
                .userId(userId)
                .currentStreak(0)
                .maxStreak(0)
                .totalXp(0L)
                .build();
    }

    private void startNewStreak(GamificationProfile profile, LocalDate date, Long xp) {
        profile.setCurrentStreak(1);
        profile.setMaxStreak(1);
        profile.setLastActivityDate(date);
        profile.setTotalXp(profile.getTotalXp() + xp + XP_STREAK_BONUS);
        log.info("🔥 Primeiro Streak Iniciado!");
    }

    private void incrementStreak(GamificationProfile profile, LocalDate date, Long xp) {
        profile.setCurrentStreak(profile.getCurrentStreak() + 1);

        if (profile.getCurrentStreak() > profile.getMaxStreak()) {
            profile.setMaxStreak(profile.getCurrentStreak());
        }

        profile.setLastActivityDate(date);

        profile.setTotalXp(profile.getTotalXp() + xp + XP_STREAK_BONUS);
        log.info("🔥 Streak AUMENTOU! Novo valor: {}", profile.getCurrentStreak());
    }

    private void resetStreak(GamificationProfile profile, LocalDate date, Long xp) {
        profile.setCurrentStreak(1); // Recomeça do 1
        profile.setLastActivityDate(date);
        profile.setTotalXp(profile.getTotalXp() + xp); // Ganha só o XP da ação, sem bônus
        log.info("❄️ Streak QUEBROU. Resetando para 1.");
    }

    private void addXp(GamificationProfile profile, Long xp) {
        profile.setTotalXp(profile.getTotalXp() + xp);
    }
}