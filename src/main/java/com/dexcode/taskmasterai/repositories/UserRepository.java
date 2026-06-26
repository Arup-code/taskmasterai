package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);

    User findByEmail(String email);

    Page<User> searchUsersByFirstNameContaining(String firstName, Pageable pageable);

    Page<User> searchUsersByLastNameContaining(String lastName, Pageable pageable);

    boolean existsByEmail(String email);
}
