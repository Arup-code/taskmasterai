package com.dexcode.taskmasterai.dto.task;

import com.dexcode.taskmasterai.enums.Priority;
import com.dexcode.taskmasterai.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskDTO {
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private Long assigneeId;
}
