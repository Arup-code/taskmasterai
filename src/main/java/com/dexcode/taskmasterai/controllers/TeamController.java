package com.dexcode.taskmasterai.controllers;

import com.dexcode.taskmasterai.dto.TeamMember.TeamMemberDTO;
import com.dexcode.taskmasterai.dto.TeamMember.UpdateMemberRoleDTO;
import com.dexcode.taskmasterai.dto.team.CreateTeamDTO;
import com.dexcode.taskmasterai.dto.team.TeamDTO;
import com.dexcode.taskmasterai.services.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/teams")
    public ResponseEntity<TeamDTO> createTeam(Principal principal, @RequestBody @Valid CreateTeamDTO createTeamDTO) {
        TeamDTO teamDTO = teamService.createTeam(principal.getName(), createTeamDTO);
        return ResponseEntity.status(201).body(teamDTO);
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<TeamDTO> getTeamById(Principal principal, @PathVariable Long id) {
        TeamDTO teamDTO = teamService.getTeamById(principal.getName(), id);
        return ResponseEntity.ok(teamDTO);
    }

    @GetMapping("/teams")
    public ResponseEntity<Page<TeamDTO>> getAllTeams(Principal principal, Pageable pageable) {
        Page<TeamDTO> teamDTOs = teamService.getAllTeams(principal.getName(), pageable);
        return ResponseEntity.ok(teamDTOs);
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<TeamDTO> updateTeam(Principal principal, @PathVariable Long id,
                                               @RequestBody @Valid CreateTeamDTO updateDTO) {
        TeamDTO updatedTeamDTO = teamService.updateTeam(principal.getName(), id, updateDTO.getName());
        return ResponseEntity.ok(updatedTeamDTO);
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<Map<String, String>> deleteTeam(Principal principal, @PathVariable Long id) {
        teamService.deleteTeam(principal.getName(), id);
        return ResponseEntity.ok(Map.of("message", "Team deleted successfully"));
    }

    @GetMapping("/teams/{id}/members")
    public ResponseEntity<Page<TeamMemberDTO>> getMembers(Principal principal,
                                                           @PathVariable Long id,
                                                           Pageable pageable) {
        Page<TeamMemberDTO> members = teamService.getMembers(principal.getName(), id, pageable);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/teams/{id}/members/{userId}")
    public ResponseEntity<Map<String, String>> removeMember(Principal principal,
                                                              @PathVariable Long id,
                                                              @PathVariable Long userId) {
        teamService.removeMember(principal.getName(), id, userId);
        return ResponseEntity.ok(Map.of("message", "Member removed successfully"));
    }

    @PutMapping("/teams/{id}/members/{userId}/role")
    public ResponseEntity<TeamMemberDTO> updateMemberRole(Principal principal,
                                                           @PathVariable Long id,
                                                           @PathVariable Long userId,
                                                           @RequestBody @Valid UpdateMemberRoleDTO dto) {
        TeamMemberDTO updated = teamService.updateMemberRole(principal.getName(), id, userId, dto);
        return ResponseEntity.ok(updated);
    }
}
