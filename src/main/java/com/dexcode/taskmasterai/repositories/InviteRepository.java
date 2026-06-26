package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.Invite;
import com.dexcode.taskmasterai.entities.Team;
import com.dexcode.taskmasterai.enums.InviteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    Page<Invite> findByTeam(Team team, Pageable pageable);
    Page<Invite> findByInviteEmail(String inviteEmail, Pageable pageable);
    Optional<Invite> findByToken(String token);
    Page<Invite> findByInviteEmailAndStatus(String inviteEmail, InviteStatus status, Pageable pageable);
}
