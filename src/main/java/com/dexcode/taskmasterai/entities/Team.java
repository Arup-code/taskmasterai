package com.dexcode.taskmasterai.entities;

import com.dexcode.taskmasterai.dto.TaskDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Set<TaskDTO> tasks;

    @OneToMany(mappedBy = "team")
    private Set<Invite> invites;

    @OneToMany(mappedBy = "team", targetEntity = TeamMember.class)
    private Set<TeamMember> members;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
