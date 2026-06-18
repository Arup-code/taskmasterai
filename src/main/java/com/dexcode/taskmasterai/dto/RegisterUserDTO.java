package com.dexcode.taskmasterai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterUserDTO {
    @NotNull( message = "Email cannot be null")
    @NotBlank( message = "Email cannot be blank")
    @NotEmpty( message = "Email cannot be empty")
    @Email( message = "Email is not valid")
    private String email;

    @NotNull( message = "Password cannot be null")
    @NotBlank( message = "Password cannot be blank")
    @NotEmpty( message = "Password cannot be empty")
    @Length(min = 8, message = "Password must be at least 8 characters long")
    @Length(max = 20, message = "Password must be at most 20 characters long")
    private String password;

    @NotNull( message = "First Name cannot be null")
    @NotBlank( message = "First Name cannot be blank")
    @NotEmpty( message = "First Name cannot be empty")
    @Length(min = 3, message = "First Name must be at least 3 characters long")
    @Length(max = 150, message = "First Name must be less than 150 characters long")
    private String firstName;

    @NotNull( message = "Last Name cannot be null")
    @NotBlank( message = "Last Name cannot be blank")
    @NotEmpty( message = "Last Name cannot be empty")
    @Length(min = 3, message = "Last Name must be at least 3 characters long")
    @Length(max = 150, message = "Last Name must be less than 150 characters long")
    private String lastName;
}
