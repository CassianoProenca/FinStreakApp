package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.UpdateUserPreferencesCommand;

public interface UpdateUserPreferencesUseCase {
    void execute(UpdateUserPreferencesCommand command);
}
