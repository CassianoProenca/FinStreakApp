package com.financial.app.application.ports.out;

import java.util.UUID;

public interface SaveUnlockedAvatarPort {
    void save(UUID userId, String avatarKey);
}
