package com.dexcode.taskmasterai.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTeamDTO {
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 3, max = 150, message = "Team name must be between 3 and 150 characters")
    private String name;
}
