package com.financial.app.application.ports.in;

import com.financial.app.application.ports.in.command.OnboardingCommand;

public interface CompleteOnboardingUseCase {
    void execute(OnboardingCommand command);
}
