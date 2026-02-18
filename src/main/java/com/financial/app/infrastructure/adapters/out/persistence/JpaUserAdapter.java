package com.financial.app.infrastructure.adapters.out.persistence;

import com.financial.app.application.ports.out.LoadAllUsersPort;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.model.User;
import com.financial.app.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.financial.app.infrastructure.adapters.out.persistence.entity.UserPreferencesEntity;
import com.financial.app.infrastructure.adapters.out.persistence.mapper.UserMapper;
import com.financial.app.infrastructure.adapters.out.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaUserAdapter implements LoadUserPort, SaveUserPort, LoadAllUsersPort {

    private final UserJpaRepository userJpaRepository;

    public JpaUserAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> loadById(UUID id) {
        return userJpaRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> loadByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        // If user has an ID, load the existing entity and update it
        if (user.getId() != null) {
            Optional<UserEntity> existingEntityOpt = userJpaRepository.findById(user.getId());
            if (existingEntityOpt.isPresent()) {
                UserEntity existingEntity = existingEntityOpt.get();

                // Update fields
                existingEntity.setName(user.getName());
                existingEntity.setEmail(user.getEmail());
                existingEntity.setPassword(user.getPassword());
                existingEntity.setOnboardingCompleted(user.isOnboardingCompleted());

                // Handle preferences
                if (user.getPreferences() != null) {
                    if (existingEntity.getPreferences() == null) {
                        UserPreferencesEntity prefsEntity = UserPreferencesEntity.builder()
                                .theme(user.getPreferences().getTheme())
                                .notificationsEnabled(user.getPreferences().isNotificationsEnabled())
                                .build();
                        prefsEntity.setUser(existingEntity);
                        existingEntity.setPreferences(prefsEntity);
                    } else {
                        existingEntity.getPreferences().setTheme(user.getPreferences().getTheme());
                        existingEntity.getPreferences().setNotificationsEnabled(user.getPreferences().isNotificationsEnabled());
                    }
                }

                UserEntity savedEntity = userJpaRepository.save(existingEntity);
                return UserMapper.toDomain(savedEntity);
            }
        }

        // For new users (no ID), create a new entity
        UserEntity entity = UserMapper.toEntity(user);
        
        // Ensure bidirectional relationship is set for preferences
        if (entity.getPreferences() != null && entity.getPreferences().getUser() == null) {
            entity.getPreferences().setUser(entity);
        }
        
        UserEntity savedEntity = userJpaRepository.save(entity);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public List<UUID> loadAllUserIds() {
        return userJpaRepository.findAll().stream()
                .map(UserEntity::getId)
                .toList();
    }
}
