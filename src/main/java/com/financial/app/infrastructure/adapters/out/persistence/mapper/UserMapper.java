package com.financial.app.infrastructure.adapters.out.persistence.mapper;

import com.financial.app.domain.model.User;
import com.financial.app.domain.model.UserPreferences;
import com.financial.app.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.financial.app.infrastructure.adapters.out.persistence.entity.UserPreferencesEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        UserPreferences prefs = null;
        if (entity.getPreferences() != null) {
            prefs = UserPreferences.builder()
                    .theme(entity.getPreferences().getTheme())
                    .notificationsEnabled(entity.getPreferences().isNotificationsEnabled())
                    .build();
        }

        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .avatarUrl(entity.getAvatarUrl())
                .onboardingCompleted(entity.isOnboardingCompleted())
                .monthlyIncome(entity.getMonthlyIncome())
                .preferences(prefs != null ? prefs : new UserPreferences())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        UserPreferencesEntity prefsEntity = null;
        if (domain.getPreferences() != null) {
            prefsEntity = UserPreferencesEntity.builder()
                    .theme(domain.getPreferences().getTheme())
                    .notificationsEnabled(domain.getPreferences().isNotificationsEnabled())
                    .build();
            // ID will be managed by JPA/Database relation usually or set if existing
        }

        UserEntity entity = UserEntity.builder()
                .name(domain.getName())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .avatarUrl(domain.getAvatarUrl())
                .onboardingCompleted(domain.isOnboardingCompleted())
                .monthlyIncome(domain.getMonthlyIncome())
                .preferences(prefsEntity)
                .build();

        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion() != null ? domain.getVersion() : 0L);

        if (prefsEntity != null) {
            prefsEntity.setUser(entity);
        }

        return entity;
    }
}
