package com.financial.app.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseDomainEntity {

    private String name;
    private String email;
    private String password;
    
    @Builder.Default
    private boolean onboardingCompleted = false;
    
    @Builder.Default
    private UserPreferences preferences = new UserPreferences();

    public void completeOnboarding() {
        this.onboardingCompleted = true;
    }
}
