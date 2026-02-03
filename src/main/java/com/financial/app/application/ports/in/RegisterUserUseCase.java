package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.RegisterUserCommand;
import com.financial.app.domain.model.User;

public interface RegisterUserUseCase {
    User execute(RegisterUserCommand command);
}
