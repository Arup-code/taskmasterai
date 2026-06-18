package com.dexcode.taskmasterai.dto;

import com.dexcode.taskmasterai.entities.Invite;
import com.dexcode.taskmasterai.entities.Task;
import com.dexcode.taskmasterai.entities.TeamMember;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TeamDTO {
    private Long id;

    private String name;

    private Set<Task> tasks;

    private Set<Invite> invites;

    private Set<UserDTO> members;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
