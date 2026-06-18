package com.dexcode.taskmasterai.controllers;

import com.dexcode.taskmasterai.dto.LoginUserDTO;
import com.dexcode.taskmasterai.dto.RegisterUserDTO;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.exceptions.auth.*;
import com.dexcode.taskmasterai.services.AuthService;
import com.dexcode.taskmasterai.services.SessionManagerService;
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

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        User user = authService.createUser(registerUserDTO);
        authService.sendVerificationEmail(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<String> verify(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/auth/{user_id}/resend")
    public ResponseEntity<String> resendVerificationEmail(@PathVariable("user_id") Long userId) {
        User user = authService.getUser(userId);
        authService.sendVerificationEmail(user);
        return ResponseEntity.ok("Verification email resent successfully");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<User> login(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        User user = authService.login(loginUserDTO);
        String authToken = sessionManagerService.createUserSession(user);
        return ResponseEntity.status(201)
                .header("X-New-Access-Token",authToken)
                .body(user);
    }

    @PostMapping("/auth/{user_id}/resetPassword")
    public ResponseEntity<User> resetPassword(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        User user = authService.login(loginUserDTO);
        String authToken = sessionManagerService.createUserSession(user);
        return ResponseEntity.status(201)
                .header("X-New-Access-Token",authToken)
                .body(user);
    }


    @ExceptionHandler(AuthVerificationException.class)
    public ResponseEntity<String> handleAuthVerificationException(AuthVerificationException e) {
        log.error("Auth Verification occurred: ", e);
        return ResponseEntity.badRequest().body("Verification failed:" +e.getMessage());
    }

    @ExceptionHandler(ExpiredVerificationTokenException.class)
    public ResponseEntity<String> handleExpiredVerificationTokenException(ExpiredVerificationTokenException e) {
        log.error("Expired Verification occurred: ", e);
        return ResponseEntity.badRequest().body("Verification failed:" +e.getMessage());
    }

    @ExceptionHandler(InvalidAuthCredentialException.class)
    public ResponseEntity<String> handleInvalidAuthCredentialException(InvalidAuthCredentialException e) {
        log.error("Invalid Auth Credential occurred: ", e);
        return ResponseEntity.badRequest().body("Verification failed:" +e.getMessage());
    }

    @ExceptionHandler(UserExistAuthException.class)
    public ResponseEntity<String> handleUserExistAuthException(UserExistAuthException e) {
        log.error("Auth Verification occurred: ", e);
        return ResponseEntity.badRequest().body("Verification failed:" +e.getMessage());
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    public ResponseEntity<String> handleVerificationTokenNotFoundException(Exception e) {
        log.error("Verification Token Not Found occurred: ", e);
        return ResponseEntity.badRequest().body("Verification failed:" +e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(Exception e) {
        log.error("Illegal Argument occurred: ", e);
        return ResponseEntity.badRequest().body("Verification failed:" +e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Method Argument Not Valid occurred: ", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(400).body(errors.toString());
    }

}
