package com.financial.app.domain.model;

import com.financial.app.domain.model.enums.NotificationType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseDomainEntity {
    private UUID userId;
    private String message;
    private NotificationType type;
    private boolean isRead;
}
