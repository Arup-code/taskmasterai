package com.dexcode.taskmasterai.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgotPasswordDTO {
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email is not valid")
    private String email;
}

