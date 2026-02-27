package com.financial.app.application.ports.in;

import java.util.List;
import java.util.UUID;

public interface GetUnlockedAvatarsUseCase {
    List<String> execute(UUID userId);
}
