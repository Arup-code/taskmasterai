package com.dexcode.taskmasterai.dto.invite;

import com.dexcode.taskmasterai.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInviteDTO {
    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    private String inviteEmail;

    @NotNull
    private Role role;
}
