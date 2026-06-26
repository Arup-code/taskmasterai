package com.dexcode.taskmasterai.controllers;

import com.dexcode.taskmasterai.components.JWTUtil;
import com.dexcode.taskmasterai.dto.auth.*;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.exceptions.auth.*;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.services.AuthService;
import com.dexcode.taskmasterai.services.SessionManagerService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionManagerService sessionManagerService;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * POST /api/auth/signup — Register a new user account.
     */
    @PostMapping("/auth/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        User user = authService.createUser(registerUserDTO);
        authService.sendVerificationEmail(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful. Please verify your email.");
        response.put("user", user);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * POST /api/auth/login — Authenticate and receive an access token.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        User user = authService.login(loginUserDTO);
        String authToken = sessionManagerService.createUserSession(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("accessToken", authToken);
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/verify — Verify email address via token.
     */
    @GetMapping("/auth/verify")
    public ResponseEntity<Map<String, String>> verify(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email verified successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/resend-verification — Resend verification email.
     */
    @PostMapping("/auth/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerificationEmail(@Valid @RequestBody ResendVerificationDTO resendDTO) {
        User user = authService.getUserByEmail(resendDTO.getEmail());
        authService.sendVerificationEmail(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Verification email resent successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/forgot-password — Request a password reset link.
     */
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        authService.sendPasswordResetEmail(forgotPasswordDTO.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "A password reset link has been sent to your email.");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/reset-password — Reset password using a valid reset token.
     */
    @PostMapping("/auth/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        authService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully. You may now log in.");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/logout — Revoke the current session.
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Extract refresh token from the JWT claims to revoke the session
                Claims claims = jwtUtil.getClaimsFromToken(token, true);
                String refreshToken = claims.get("refresh_token", String.class);
                if (refreshToken != null) {
                    sessionManagerService.revokeUserSession(refreshToken);
                }
            } catch (Exception e) {
                log.warn("Could not parse token during logout: {}", e.getMessage());
            }
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    // ──────────────────────────────────────────────
    // Exception Handlers
    // ──────────────────────────────────────────────

    @ExceptionHandler(AuthVerificationException.class)
    public ResponseEntity<Map<String, String>> handleAuthVerificationException(AuthVerificationException e) {
        log.error("Auth Verification error: ", e);
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ExpiredVerificationTokenException.class)
    public ResponseEntity<Map<String, String>> handleExpiredVerificationTokenException(ExpiredVerificationTokenException e) {
        log.error("Expired Verification Token: ", e);
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(InvalidAuthCredentialException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAuthCredentialException(InvalidAuthCredentialException e) {
        log.error("Invalid Auth Credential: ", e);
        return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(UserExistAuthException.class)
    public ResponseEntity<Map<String, String>> handleUserExistAuthException(UserExistAuthException e) {
        log.error("User already exists: ", e);
        return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleVerificationTokenNotFoundException(VerificationTokenNotFoundException e) {
        log.error("Verification Token Not Found: ", e);
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException e) {
        log.error("User Not Found: ", e);
        return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal Argument: ", e);
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Validation error: ", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("details", errors);
        return ResponseEntity.status(400).body(response);
    }

}
