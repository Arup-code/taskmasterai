package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {

    UserVerification getUserVerificationsByVerificationToken(String verificationToken);
}
