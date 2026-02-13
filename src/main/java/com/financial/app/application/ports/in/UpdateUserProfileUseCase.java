package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.UpdateUserProfileCommand;
import com.financial.app.domain.model.User;

public interface UpdateUserProfileUseCase {
    User execute(UpdateUserProfileCommand command);
}
