package com.dexcode.taskmasterai.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @UniqueElements
    private String refreshToken;

    private String userAgent;

    private String ipAddress;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime revokedAt;
}
