package com.financial.app.application.ports.out;

import com.financial.app.domain.model.User;

public interface SaveUserPort {
    User save(User user);
}
