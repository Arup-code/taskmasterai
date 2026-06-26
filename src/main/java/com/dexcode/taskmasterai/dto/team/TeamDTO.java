package com.dexcode.taskmasterai.dto.team;

import com.dexcode.taskmasterai.dto.TeamMember.TeamMemberDTO;
import com.dexcode.taskmasterai.dto.invite.InviteDTO;
import com.dexcode.taskmasterai.dto.task.TaskDTO;
import com.dexcode.taskmasterai.entities.Invite;
import com.dexcode.taskmasterai.entities.Team;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TeamDTO {
    private Long id;

    private String name;

    private Set<TaskDTO> tasks;

    private Set<InviteDTO> invites;

    private Set<TeamMemberDTO> members;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.createdAt = team.getCreatedAt();
        this.updatedAt = team.getUpdatedAt();
    }
}
