package com.financial.app.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "user_unlocked_avatars",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "avatar_key"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUnlockedAvatarEntity extends AbstractBaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "avatar_key", nullable = false, length = 100)
    private String avatarKey;
}
