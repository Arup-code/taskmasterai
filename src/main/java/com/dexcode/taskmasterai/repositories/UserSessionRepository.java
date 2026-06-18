package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    UserSession findUserSessionByUser(User user);

    Optional<UserSession> findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);
}
