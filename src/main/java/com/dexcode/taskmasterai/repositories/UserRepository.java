package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);

    User findByEmail(String email);

    User searchUsersByFirstNameContaining(String firstName);

    User searchUsersByLastNameContaining(String lastName);
}
