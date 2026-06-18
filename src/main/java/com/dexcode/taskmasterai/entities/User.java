package com.dexcode.taskmasterai.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table( name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    private String email;

    @NotNull
    @Builder.Default
    private Boolean is_verified = false;

    @JsonIgnore
    @NotNull
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserSession> userSessions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserVerification> userVerifications;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private Set<Task> createdTasks;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL)
    private Set<Task> assignedTasks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Invite> invites;

    @OneToMany(mappedBy = "user", targetEntity = TeamMember.class, cascade = CascadeType.ALL)
    private Set<Team> teams;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

}
