package com.dexcode.taskmasterai.dto;

import com.dexcode.taskmasterai.entities.Comment;
import com.dexcode.taskmasterai.entities.TaskAttachment;
import com.dexcode.taskmasterai.entities.Team;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.enums.Priority;
import com.dexcode.taskmasterai.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TaskDTO {
    private Long id;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @NotNull
    private String title;

    @NotNull
    private String description;

    private UserDTO creator;

    private UserDTO assignee;

    private Set<Comment> comments;

    private Set<TaskAttachment> attachments;

    private LocalDateTime dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
