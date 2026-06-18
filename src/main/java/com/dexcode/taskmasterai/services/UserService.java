package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public User findUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

}
