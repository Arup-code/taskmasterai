package com.dexcode.taskmasterai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentDTO {
    @NotNull
    @NotEmpty
    @NotBlank
    private String content;
}
