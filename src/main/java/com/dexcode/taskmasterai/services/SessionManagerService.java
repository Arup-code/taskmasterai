package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.components.JWTUtil;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.entities.UserSession;
import com.dexcode.taskmasterai.exceptions.session.SessionExpiredException;
import com.dexcode.taskmasterai.exceptions.session.SessionNotFoundException;
import com.dexcode.taskmasterai.exceptions.session.SessionRevokedException;
import com.dexcode.taskmasterai.repositories.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionManagerService {
    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * Checks if the user's session is valid.
     */
    public boolean checkUserSessionValidity(String refreshToken) throws SessionNotFoundException, SessionRevokedException, SessionExpiredException {
        UserSession userSession = userSessionRepository.findByRefreshToken(refreshToken).orElse(null);
        if(userSession == null) {
            throw new SessionNotFoundException("Invalid refresh token. Please login again.");
        }
        if(userSession.getRevokedAt() != null) {
            throw new SessionRevokedException("This session has been revoked. Please login again.");
        }
        if(userSession.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new SessionExpiredException("This session has expired. Please login again.");
        }
        return true;
    }

    public UserSession getUserSession(String refreshToken) throws SessionNotFoundException {
        UserSession userSession = userSessionRepository.findByRefreshToken(refreshToken).orElse(null);
        if(userSession == null) {
            throw new SessionNotFoundException("Invalid refresh token. Please login again.");
        }
        return userSession;
    }

    public String createUserSession(User user) {
        String refreshToken = jwtUtil.generateRefreshToken();
        UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.setRefreshToken(refreshToken);
        userSession.setExpiresAt(java.time.LocalDateTime.now().plusDays(jwtUtil.getAccessTokenExpiry()));
        userSessionRepository.save(userSession);
        return jwtUtil.generateAccessToken(user,refreshToken);
    }

    public void revokeUserSession(String refreshToken) throws SessionNotFoundException {
        UserSession userSession = userSessionRepository.findByRefreshToken(refreshToken).orElse(null);
        if(userSession == null) {
            throw new SessionNotFoundException("Invalid refresh token. Please login again.");
        }
        userSession.setRevokedAt(java.time.LocalDateTime.now());
        userSessionRepository.save(userSession);
    }

    public void revokeAllUserSessions(User user) {
        user.getUserSessions().forEach(session -> {
            if(session.getRevokedAt() == null) {
                session.setRevokedAt(java.time.LocalDateTime.now());
            }
            userSessionRepository.save(session);
        });
    }

}
