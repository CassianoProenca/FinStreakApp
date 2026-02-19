package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.RegisterUserUseCase;
import com.financial.app.application.ports.in.command.RegisterUserCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.exception.BusinessException;
import com.financial.app.domain.model.User;
import com.financial.app.domain.model.UserPreferences;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserService implements RegisterUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    public RegisterUserService(LoadUserPort loadUserPort, SaveUserPort saveUserPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
    }

    @Override
    public User execute(RegisterUserCommand command) {
        if (loadUserPort.loadByEmail(command.email()).isPresent()) {
            throw new BusinessException("E-mail já cadastrado");
        }

        User newUser = User.builder()
                .name(command.name())
                .email(command.email())
                .password(command.password())
                .onboardingCompleted(false)
                .avatarUrl("https://ui-avatars.com/api/?name=" + command.name().replace(" ", "+") + "&background=random")
                .preferences(new UserPreferences())
                .build();
        
        // newUser.initialize(); - Removed to let JPA generate ID

        return saveUserPort.save(newUser);
    }
}
