package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.Team;
import com.dexcode.taskmasterai.entities.TeamMember;
import com.dexcode.taskmasterai.entities.TeamMemberId;
import com.dexcode.taskmasterai.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {
    Page<TeamMember> findByUser(User user, Pageable pageable);
    Optional<TeamMember> findByUserAndTeam(User user, Team team);
    Page<TeamMember> findByTeam(Team team, Pageable pageable);
}
