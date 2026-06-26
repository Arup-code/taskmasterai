package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.Team;
import com.dexcode.taskmasterai.entities.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByMembersContains(Set<TeamMember> members);
}
