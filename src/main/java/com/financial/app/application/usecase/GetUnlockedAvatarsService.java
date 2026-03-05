package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.GetUnlockedAvatarsUseCase;
import com.financial.app.application.ports.out.LoadUnlockedAvatarsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUnlockedAvatarsService implements GetUnlockedAvatarsUseCase {

    private final LoadUnlockedAvatarsPort loadUnlockedAvatarsPort;

    @Override
    public List<String> execute(UUID userId) {
        List<String> avatarKeys = loadUnlockedAvatarsPort.loadAvatarKeys(userId);
        if (avatarKeys.isEmpty()) {
            return List.of("default_avatar");
        }
        return avatarKeys;
    }
}
