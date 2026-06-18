package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.components.JWTUtil;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    public String generateNewAuthToken(Long userId, String refreshToken) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }
        return jwtUtil.generateAccessToken(user,refreshToken);
    }

}
