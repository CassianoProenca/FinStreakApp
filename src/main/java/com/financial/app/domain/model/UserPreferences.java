package com.financial.app.domain.model;

import com.financial.app.domain.model.enums.Theme;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    @Builder.Default
    private Theme theme = Theme.LIGHT;
    
    @Builder.Default
    private boolean notificationsEnabled = true;
}
