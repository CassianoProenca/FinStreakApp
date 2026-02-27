package com.financial.app.application.ports.out;

import java.util.List;
import java.util.UUID;

public interface LoadUnlockedAvatarsPort {
    List<String> loadAvatarKeys(UUID userId);
}
