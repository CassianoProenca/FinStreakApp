package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.UpdateUserPreferencesUseCase;
import com.financial.app.application.ports.in.command.UpdateUserPreferencesCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.model.User;
import com.financial.app.domain.model.UserPreferences;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateUserPreferencesService implements UpdateUserPreferencesUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    public UpdateUserPreferencesService(LoadUserPort loadUserPort, SaveUserPort saveUserPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
    }

    @Override
    public void execute(UpdateUserPreferencesCommand command) {
        User user = loadUserPort.loadById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPreferences newPrefs = UserPreferences.builder()
                .theme(command.theme())
                .notificationsEnabled(command.notificationsEnabled())
                .build();

        user.setPreferences(newPrefs);

        saveUserPort.save(user);
    }
}
