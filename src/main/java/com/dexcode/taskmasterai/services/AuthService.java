package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.components.SMTPUtil;
import com.dexcode.taskmasterai.config.AuthConfig;
import com.dexcode.taskmasterai.dto.auth.LoginUserDTO;
import com.dexcode.taskmasterai.dto.auth.RegisterUserDTO;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.entities.UserVerification;
import com.dexcode.taskmasterai.exceptions.auth.*;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.exceptions.user.UserNotVerifiedException;
import com.dexcode.taskmasterai.repositories.UserRepository;
import com.dexcode.taskmasterai.repositories.UserVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private SMTPUtil smtpUtil;

    @Value("${application.url}")
    private String baseUrl;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserVerificationRepository userVerificationRepository;

    /**
     * Checks if the user exists in the database. If not, creates a new user.
     */
    public User createUser(RegisterUserDTO registerUserDTO) throws UserExistAuthException {
        User existing_user = userRepository.findByEmail(registerUserDTO.getEmail());

        if(existing_user != null) {
            throw new UserExistAuthException("User with this email already exists.");
        }

        String hashedPassword = authConfig.passwordEncoder().encode(registerUserDTO.getPassword());

        User user = User.builder()
                .email(registerUserDTO.getEmail())
                .password(hashedPassword)
                .firstName(registerUserDTO.getFirstName())
                .lastName(registerUserDTO.getLastName())
                .created_at(java.time.LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    /**
     * Generates a verification token and sends it to the user's email.
     */
    public void sendVerificationEmail(User user) throws AuthVerificationException {
        try {
            String token = UUID.randomUUID().toString();

            UserVerification userVerification = UserVerification.builder()
                    .user(user)
                    .verificationToken(token)
                    .createdAt(java.time.LocalDateTime.now())
                    .expiresAt(java.time.LocalDateTime.now().plusMinutes(30))
                    .build();

            userVerificationRepository.save(userVerification);

            String verificationUrl = baseUrl+"/api/auth/verify?token="+token;

            smtpUtil.sendEmail(user.getEmail(), "Verify your email", "Please verify your email by clicking the link below: \n " + verificationUrl);

        } catch (Exception e) {
            throw new AuthVerificationException("Error sending verification token: " + e.toString());
        }

    }

    /**
     * Verifies the user's email by updating the user's is_verified field.
     */
    public boolean verifyEmail(String verificationToken) throws VerificationTokenNotFoundException,ExpiredVerificationTokenException {
        UserVerification userVerification = userVerificationRepository.getUserVerificationsByVerificationToken(verificationToken);
        if(userVerification == null) {
            throw new VerificationTokenNotFoundException("Invalid verification token");
        }
        if(userVerification.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new ExpiredVerificationTokenException("Verification token has expired");
        }
        if(!userVerification.getUser().getIs_verified()) {
            userVerification.getUser().setIs_verified(true);
            userRepository.save(userVerification.getUser());
        }
        userVerificationRepository.delete(userVerification);
        return true;
    }

    /**
     * Authenticates a user with email and password.
     */
    public User login(LoginUserDTO loginUserDTO) throws UserNotFoundException, UserNotVerifiedException, InvalidAuthCredentialException {
        User user = userRepository.findByEmail(loginUserDTO.getEmail());
        if(user == null) {
            throw new UserNotFoundException("No account found with this email. Please register first.");
        }
        if(user.getIs_verified() == false) {
            throw new UserNotVerifiedException("Please verify your email first.");
        }
        if(!authConfig.passwordEncoder().matches(loginUserDTO.getPassword(), user.getPassword())) {
            throw new InvalidAuthCredentialException("Invalid email or password");
        }
        return user;
    }

    public User getUser(Long userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new UserNotFoundException("No account found with this id.");
        }
        return user;
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UserNotFoundException("No account found with this email. Please register first.");
        }
        return user;
    }

    /**
     * Sends a password-reset token to the user's email.
     */
    public void sendPasswordResetEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UserNotFoundException("No account found with this email. Please register first.");
        }

        String token = UUID.randomUUID().toString();

        UserVerification userVerification = UserVerification.builder()
                .user(user)
                .verificationToken(token)
                .createdAt(java.time.LocalDateTime.now())
                .expiresAt(java.time.LocalDateTime.now().plusMinutes(30))
                .build();

        userVerificationRepository.save(userVerification);

        String resetUrl = baseUrl + "/api/auth/reset-password?token=" + token;

        smtpUtil.sendEmail(user.getEmail(), "Reset your password",
                "Please reset your password by clicking the link below: \n" + resetUrl + "\nThis link expires in 30 minutes.");
    }

    /**
     * Resets the user's password using a valid reset token.
     */
    public void resetPassword(String token, String newPassword) throws VerificationTokenNotFoundException, ExpiredVerificationTokenException {
        UserVerification userVerification = userVerificationRepository.getUserVerificationsByVerificationToken(token);
        if(userVerification == null) {
            throw new VerificationTokenNotFoundException("Invalid or expired password reset token.");
        }
        if(userVerification.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new ExpiredVerificationTokenException("Password reset token has expired.");
        }

        User user = userVerification.getUser();
        user.setPassword(authConfig.passwordEncoder().encode(newPassword));
        userRepository.save(user);
        userVerificationRepository.delete(userVerification);
    }
}
