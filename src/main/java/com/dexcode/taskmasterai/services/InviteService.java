package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.dto.invite.CreateInviteDTO;
import com.dexcode.taskmasterai.dto.invite.InviteDTO;
import com.dexcode.taskmasterai.entities.*;
import com.dexcode.taskmasterai.enums.InviteStatus;
import com.dexcode.taskmasterai.enums.Role;
import com.dexcode.taskmasterai.exceptions.invite.InviteAlreadyAcceptedException;
import com.dexcode.taskmasterai.exceptions.invite.InviteEmailMismatchException;
import com.dexcode.taskmasterai.exceptions.invite.InviteNotFoundException;
import com.dexcode.taskmasterai.exceptions.team.InsufficientPriviledgeTeamException;
import com.dexcode.taskmasterai.exceptions.team.TeamNotFoundException;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.repositories.InviteRepository;
import com.dexcode.taskmasterai.repositories.TeamMemberRepository;
import com.dexcode.taskmasterai.repositories.TeamRepository;
import com.dexcode.taskmasterai.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class InviteService {

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public InviteDTO createInvite(String email, Long teamId, CreateInviteDTO dto) {
        User inviter = userRepository.findByEmail(email);
        if (inviter == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        TeamMember inviterMembership = teamMemberRepository.findByUserAndTeam(inviter, team)
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        if (inviterMembership.getRole() != Role.ADMIN) {
            throw new InsufficientPriviledgeTeamException("Only admins can invite members");
        }

        Invite invite = Invite.builder()
                .user(inviter)
                .team(team)
                .inviteEmail(dto.getInviteEmail())
                .role(dto.getRole())
                .token(UUID.randomUUID().toString())
                .status(InviteStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        inviteRepository.save(invite);
        return new InviteDTO(invite);
    }

    public Page<InviteDTO> getTeamInvites(String email, Long teamId, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        return inviteRepository.findByTeam(team, pageable).map(InviteDTO::new);
    }

    public Page<InviteDTO> getUserInvites(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return inviteRepository.findByInviteEmailAndStatus(email, InviteStatus.PENDING, pageable)
                .map(InviteDTO::new);
    }

    @Transactional
    public void cancelInvite(String email, Long inviteId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new InviteNotFoundException("Invite not found"));

        TeamMember membership = teamMemberRepository.findByUserAndTeam(user, invite.getTeam())
                .orElseThrow(() -> new TeamNotFoundException("You are not a member of this team"));

        if (membership.getRole() != Role.ADMIN) {
            throw new InsufficientPriviledgeTeamException("Only admins can cancel invites");
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new InviteAlreadyAcceptedException("Invite is no longer pending");
        }

        invite.setStatus(InviteStatus.CANCELLED);
        inviteRepository.save(invite);
    }

    @Transactional
    public InviteDTO acceptInvite(String email, String token) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new InviteNotFoundException("Invalid invite token"));

        if (!invite.getInviteEmail().equalsIgnoreCase(email)) {
            throw new InviteEmailMismatchException("This invite is for a different email address");
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new InviteAlreadyAcceptedException("This invite has already been " + invite.getStatus().name().toLowerCase());
        }

        TeamMember teamMember = TeamMember.builder()
                .user(user)
                .team(invite.getTeam())
                .role(invite.getRole())
                .joinedAt(LocalDateTime.now())
                .build();

        teamMemberRepository.save(teamMember);

        invite.setStatus(InviteStatus.ACCEPTED);
        inviteRepository.save(invite);

        return new InviteDTO(invite);
    }
}
