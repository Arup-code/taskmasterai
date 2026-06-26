package com.dexcode.taskmasterai.dto.team;

import com.dexcode.taskmasterai.dto.task.TaskDTO;
import com.dexcode.taskmasterai.dto.user.UserDTO;
import com.dexcode.taskmasterai.entities.Invite;
import com.dexcode.taskmasterai.entities.Task;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public class TeamRefDTO {
    @NotNull
    private Long id;

    @NotNull
    @NotEmpty
    @NotBlank
    @Min(value = 3, message = "Team name must be at least 3 characters long")
    @Min(value = 150, message = "Team name must be less than 150 characters long")
    private String name;

    private Set<TaskDTO> tasks;

    private Set<Invite> invites;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
