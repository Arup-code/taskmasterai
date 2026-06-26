package com.dexcode.taskmasterai.dto.task;

import com.dexcode.taskmasterai.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskDTO {
    @NotNull
    @NotEmpty
    @NotBlank
    private String title;

    @NotNull
    @NotEmpty
    @NotBlank
    private String description;

    private Priority priority;

    private LocalDateTime dueDate;

    private Long assigneeId;
}
