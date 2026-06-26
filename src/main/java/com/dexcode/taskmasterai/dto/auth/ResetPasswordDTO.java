package com.dexcode.taskmasterai.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ResetPasswordDTO {
    @NotNull(message = "Token cannot be null")
    @NotBlank(message = "Token cannot be blank")
    @NotEmpty(message = "Token cannot be empty")
    private String token;

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @NotEmpty(message = "Password cannot be empty")
    @Length(min = 8, message = "Password must be at least 8 characters long")
    @Length(max = 20, message = "Password must be at most 20 characters long")
    private String newPassword;
}

