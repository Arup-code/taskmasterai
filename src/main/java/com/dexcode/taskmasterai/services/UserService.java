package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.config.AuthConfig;
import com.dexcode.taskmasterai.dto.user.ChangePasswordDTO;
import com.dexcode.taskmasterai.dto.user.UpdateUserDTO;
import com.dexcode.taskmasterai.dto.user.UserDTO;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.exceptions.user.InvalidPasswordException;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthConfig authConfig;

    public UserDTO getProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return new UserDTO(user);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO updateProfile(String email, UpdateUserDTO dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUpdated_at(LocalDateTime.now());

        userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordDTO dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        if (!authConfig.passwordEncoder().matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPassword(authConfig.passwordEncoder().encode(dto.getNewPassword()));
        user.setUpdated_at(LocalDateTime.now());
        userRepository.save(user);
    }

    public Page<UserDTO> searchUsers(String query, Pageable pageable) {
        Page<User> byFirstName = userRepository.searchUsersByFirstNameContaining(query, pageable);
        if (byFirstName.hasContent()) {
            return byFirstName.map(UserDTO::new);
        }
        return userRepository.searchUsersByLastNameContaining(query, pageable).map(UserDTO::new);
    }
}
