package com.financial.app.application.ports.out;

import com.financial.app.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {
    Optional<User> loadById(UUID id);
    Optional<User> loadByEmail(String email);
}
