package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.components.SMTPUtil;
import com.dexcode.taskmasterai.config.AuthConfig;
import com.dexcode.taskmasterai.dto.LoginUserDTO;
import com.dexcode.taskmasterai.dto.RegisterUserDTO;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.entities.UserVerification;
import com.dexcode.taskmasterai.exceptions.auth.*;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.exceptions.user.UserNotVerifiedException;
import com.dexcode.taskmasterai.repositories.UserRepository;
import com.dexcode.taskmasterai.repositories.UserSessionRepository;
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

            String verificationUrl = baseUrl+"/auth/verify?token="+token;

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
        }
        userVerificationRepository.delete(userVerification);
        return true;
    }

    /**
     * Verifies the user's email by updating the user's is_verified field.
     */
    public User login(LoginUserDTO loginUserDTO) throws UserNotFoundException, UserNotVerifiedException, InvalidAuthCredentialException {
        User user = userRepository.findByEmail(loginUserDTO.getEmail());
        if(user == null) {
            throw new UserNotFoundException("No account found with this email. Please register first.");
        }
        if(user.getIs_verified() == false) {
            throw new UserNotVerifiedException("Please verify your email first.");
        }
        String hashedPassword = authConfig.passwordEncoder().encode(loginUserDTO.getPassword());
        if(!hashedPassword.equals(user.getPassword())) {
            throw new InvalidAuthCredentialException("Invalid email or password");
        }
        return user;
    }

    public User getUser(Long userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new UserNotFoundException("No account found with this email. Please register first.");
        }
        return user;
    }

}
