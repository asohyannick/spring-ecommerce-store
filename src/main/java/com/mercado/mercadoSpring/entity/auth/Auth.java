package com.mercado.mercadoSpring.entity.auth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mercado.mercadoSpring.constants.user.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role = UserRole.CUSTOMER;

    @Builder.Default
    @JsonIgnore
    @Column(nullable = false)
    private Boolean isAccountBlocked = false;

    @Builder.Default
    @JsonIgnore
    @Column(nullable = false)
    private Boolean isEmailVerified = false;

    @JsonIgnore
    @Column
    private String refreshToken;

    @JsonIgnore
    @Column
    private String accessToken;

    @JsonIgnore
    @Column
    private String twoFactorSecret;

    @Builder.Default
    @JsonIgnore
    @Column(nullable = false)
    private Boolean isTwoFactorVerified = false;

    @JsonIgnore
    @Column
    private LocalDateTime twoFactorExpiry;

    @Builder.Default
    @JsonIgnore
    @Column(nullable = false)
    private Integer twoFactorAttempts = 0;

    @Builder.Default
    @JsonIgnore
    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @JsonIgnore
    @Column
    private String magicToken;

    @JsonIgnore
    @Column
    private LocalDateTime magicTokenExpiration;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
