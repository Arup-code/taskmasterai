package com.dexcode.taskmasterai.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotNull
    @NotEmpty
    @NotBlank
    private String currentPassword;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
