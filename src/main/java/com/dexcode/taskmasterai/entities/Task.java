package com.dexcode.taskmasterai.entities;

import com.dexcode.taskmasterai.enums.Priority;
import com.dexcode.taskmasterai.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @ManyToOne()
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne()
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne()
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<TaskAttachment> attachments;

    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

