package com.financial.app.application.ports.out;

import java.util.List;
import java.util.UUID;

public interface LoadAllUsersPort {
    List<UUID> loadAllUserIds();
}
