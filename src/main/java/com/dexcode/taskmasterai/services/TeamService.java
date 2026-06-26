package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.dto.TeamMember.TeamMemberDTO;
import com.dexcode.taskmasterai.dto.TeamMember.UpdateMemberRoleDTO;
import com.dexcode.taskmasterai.dto.team.CreateTeamDTO;
import com.dexcode.taskmasterai.dto.team.TeamDTO;
import com.dexcode.taskmasterai.entities.Team;
import com.dexcode.taskmasterai.entities.TeamMember;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.enums.Role;
import com.dexcode.taskmasterai.exceptions.team.InsufficientPriviledgeTeamException;
import com.dexcode.taskmasterai.exceptions.team.MemberNotFoundException;
import com.dexcode.taskmasterai.exceptions.team.TeamNotFoundException;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.repositories.TeamMemberRepository;
import com.dexcode.taskmasterai.repositories.TeamRepository;
import com.dexcode.taskmasterai.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TeamDTO createTeam(String email, CreateTeamDTO createTeamDTO) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = Team.builder()
                .name(createTeamDTO.getName())
                .createdAt(LocalDateTime.now())
                .build();

        teamRepository.save(team);

        TeamMember teamMember = TeamMember.builder()
                .user(user)
                .team(team)
                .role(Role.ADMIN)
                .joinedAt(LocalDateTime.now())
                .build();

        teamMemberRepository.save(teamMember);

        return new TeamDTO(team);
    }

    public TeamDTO getTeamById(String email, Long id) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        return new TeamDTO(team);
    }

    public Page<TeamDTO> getAllTeams(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return teamMemberRepository.findByUser(user, pageable)
                .map(TeamMember::getTeam)
                .map(TeamDTO::new);
    }

    @Transactional
    public TeamDTO updateTeam(String email, Long teamId, String newName) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        TeamMember membership = teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        if (membership.getRole() != Role.ADMIN) {
            throw new InsufficientPriviledgeTeamException("Only admins can update the team");
        }

        team.setName(newName);
        team.setUpdatedAt(LocalDateTime.now());
        teamRepository.save(team);

        return new TeamDTO(team);
    }

    @Transactional
    public void deleteTeam(String email, Long teamId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        TeamMember membership = teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        if (membership.getRole() != Role.ADMIN) {
            throw new InsufficientPriviledgeTeamException("Only admins can delete the team");
        }

        teamRepository.delete(team);
    }

    public Page<TeamMemberDTO> getMembers(String email, Long teamId, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        return teamMemberRepository.findByTeam(team, pageable).map(TeamMemberDTO::new);
    }

    @Transactional
    public void removeMember(String email, Long teamId, Long memberUserId) {
        User admin = userRepository.findByEmail(email);
        if (admin == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        TeamMember adminMembership = teamMemberRepository.findByUserAndTeam(admin, team)
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        if (adminMembership.getRole() != Role.ADMIN) {
            throw new InsufficientPriviledgeTeamException("Only admins can remove members");
        }

        User memberUser = userRepository.findById(memberUserId)
                .orElseThrow(() -> new UserNotFoundException("Member user not found"));

        TeamMember memberToRemove = teamMemberRepository.findByUserAndTeam(memberUser, team)
                .orElseThrow(() -> new MemberNotFoundException("User is not a member of this team"));

        if (memberToRemove.getRole() == Role.ADMIN) {
            throw new InsufficientPriviledgeTeamException("Cannot remove the team admin");
        }

        teamMemberRepository.delete(memberToRemove);
    }

    @Transactional
    public TeamMemberDTO updateMemberRole(String email, Long teamId, Long memberUserId, UpdateMemberRoleDTO dto) {
        User admin = userRepository.findByEmail(email);
        if (admin == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        TeamMember adminMembership = teamMemberRepository.findByUserAndTeam(admin, team)
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        if (adminMembership.getRole() != Role.ADMIN) {
            throw new InsufficientPriviledgeTeamException("Only admins can change member roles");
        }

        User memberUser = userRepository.findById(memberUserId)
                .orElseThrow(() -> new UserNotFoundException("Member user not found"));

        TeamMember member = teamMemberRepository.findByUserAndTeam(memberUser, team)
                .orElseThrow(() -> new MemberNotFoundException("User is not a member of this team"));

        member.setRole(dto.getRole());
        teamMemberRepository.save(member);

        return new TeamMemberDTO(member);
    }
}
