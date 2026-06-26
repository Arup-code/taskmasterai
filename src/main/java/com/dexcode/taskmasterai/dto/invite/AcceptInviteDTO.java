package com.dexcode.taskmasterai.dto.invite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptInviteDTO {
    @NotNull
    @NotEmpty
    @NotBlank
    private String token;
}
