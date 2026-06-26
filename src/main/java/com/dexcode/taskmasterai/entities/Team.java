package com.dexcode.taskmasterai.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 3, max = 150, message = "Team name must be between 3 and 150 characters")
    private String name;

    @OneToMany(mappedBy = "team")
    private Set<Task> tasks;

    @OneToMany(mappedBy = "team")
    private Set<Invite> invites;

    @OneToMany(mappedBy = "team", targetEntity = TeamMember.class)
    private Set<TeamMember> members;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
