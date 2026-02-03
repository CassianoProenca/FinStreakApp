package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.CompleteOnboardingUseCase;
import com.financial.app.application.ports.in.command.OnboardingCommand;
import com.financial.app.application.ports.out.LoadUserPort;
import com.financial.app.application.ports.out.SaveUserPort;
import com.financial.app.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompleteOnboardingService implements CompleteOnboardingUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    public CompleteOnboardingService(LoadUserPort loadUserPort, SaveUserPort saveUserPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
    }

    @Override
    public void execute(OnboardingCommand command) {
        User user = loadUserPort.loadById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.completeOnboarding();
        
        // In a real scenario, we might use monthlyIncome and monthlySavingsGoal
        // to create initial budget/goals here or store them in preferences.
        // For MVP, we simply mark onboarding as complete.

        saveUserPort.save(user);
    }
}
