package com.financial.app.repositories;

import com.financial.app.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    List<Achievement> findByUserId(UUID userId);
}
