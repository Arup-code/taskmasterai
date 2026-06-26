package com.dexcode.taskmasterai.dto.user;

import com.dexcode.taskmasterai.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long id;

    @NotNull
    @NotEmpty
    @NotBlank
    private String firstName;

    @NotNull
    @NotEmpty
    @NotBlank
    private String lastName;

    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    private String email;

    @NotNull
    private Boolean is_verified = false;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.is_verified = user.getIs_verified();
        this.created_at = user.getCreated_at();
        this.updated_at = user.getUpdated_at();
    }
}
