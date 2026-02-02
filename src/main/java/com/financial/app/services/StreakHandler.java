package com.financial.app.services;

import com.financial.app.events.TransactionCreatedEvent;
import com.financial.app.model.GamificationProfile;
import com.financial.app.repositories.GamificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreakHandler {

    private final GamificationRepository repository;


    @EventListener
    @Transactional
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        log.info("🎮 Evento recebido! Processando gamificação para User: {}", event.userId());

        // 1. Busca o perfil ou cria um novo (Onboarding silencioso)
        GamificationProfile profile = repository.findByUserId(event.userId())
                .orElseGet(() -> createNewProfile(event.userId()));

        // 2. Lógica de Data (Ignora hora, só importa o dia)
        LocalDate transactionDate = event.date().toLocalDate();
        LocalDate lastActivity = profile.getLastActivityDate();

        // 3. O algoritmo do Streak
        if (lastActivity == null) {
            // Primeira vez usando
            incrementStreak(profile, transactionDate);
        } else if (lastActivity.isEqual(transactionDate)) {
            // Já usou hoje? Só ganha XP, não aumenta streak
            addXp(profile, 10L); // XP por transação extra
        } else if (lastActivity.plusDays(1).isEqual(transactionDate)) {
            // Usou ontem e usou hoje? Aumenta o fogo! 🔥
            incrementStreak(profile, transactionDate);
        } else {
            // Quebrou o streak (tristeza) 😢
            resetStreak(profile, transactionDate);
        }

        repository.save(profile);
    }

    private GamificationProfile createNewProfile(java.util.UUID userId) {
        return GamificationProfile.builder()
                .userId(userId)
                .currentStreak(0)
                .maxStreak(0)
                .totalXp(0L)
                .build();
    }

    private void incrementStreak(GamificationProfile profile, LocalDate date) {
        profile.setCurrentStreak(profile.getCurrentStreak() + 1);

        // Atualiza o recorde pessoal se necessário
        if (profile.getCurrentStreak() > profile.getMaxStreak()) {
            profile.setMaxStreak(profile.getCurrentStreak());
        }

        profile.setLastActivityDate(date);
        profile.setTotalXp(profile.getTotalXp() + 50L); // Bônus por manter o dia
        log.info("🔥 Streak AUMENTOU! Novo valor: {}", profile.getCurrentStreak());
    }

    private void resetStreak(GamificationProfile profile, LocalDate date) {
        profile.setCurrentStreak(1); // Recomeça do 1 (o dia de hoje conta)
        profile.setLastActivityDate(date);
        profile.setTotalXp(profile.getTotalXp() + 10L); // XP de consolação
        log.info("❄️ Streak QUEBROU. Resetando para 1.");
    }

    private void addXp(GamificationProfile profile, Long xp) {
        profile.setTotalXp(profile.getTotalXp() + xp);
    }
}