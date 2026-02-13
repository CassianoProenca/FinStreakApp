package com.financial.app.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends AbstractBaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(nullable = false)
    private boolean onboardingCompleted;

    @Column(name = "monthly_income", precision = 19, scale = 2)
    private java.math.BigDecimal monthlyIncome;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "preferences_id", referencedColumnName = "id")
    private UserPreferencesEntity preferences;
}
