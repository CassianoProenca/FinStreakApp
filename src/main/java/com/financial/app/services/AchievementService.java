package com.financial.app.services;

import com.financial.app.model.Achievement;
import com.financial.app.repositories.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;

    @Transactional
    public void unlockAchievement(Achievement achievement) {
        // Here we could check if user already has it to avoid duplicates
        // For MVP we just save
        achievement.setEarnedAt(LocalDateTime.now());
        achievementRepository.save(achievement);
    }
}
