package com.dexcode.taskmasterai.dto.task;

import com.dexcode.taskmasterai.dto.CommentDTO;
import com.dexcode.taskmasterai.dto.attachment.AttachmentDTO;
import com.dexcode.taskmasterai.dto.user.UserDTO;
import com.dexcode.taskmasterai.entities.Task;
import com.dexcode.taskmasterai.enums.Priority;
import com.dexcode.taskmasterai.enums.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private TaskStatus status;
    private Priority priority;
    private String title;
    private String description;
    private UserDTO creator;
    private UserDTO assignee;
    private Set<CommentDTO> comments;
    private Set<AttachmentDTO> attachments;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskDTO(Task task) {
        id = task.getId();
        status = task.getStatus();
        priority = task.getPriority();
        title = task.getTitle();
        description = task.getDescription();
        creator = task.getCreator() != null ? new UserDTO(task.getCreator()) : null;
        assignee = task.getAssignee() != null ? new UserDTO(task.getAssignee()) : null;
        comments = task.getComments() != null
                ? task.getComments().stream().map(CommentDTO::new).collect(Collectors.toSet())
                : Collections.emptySet();
        attachments = task.getAttachments() != null
                ? task.getAttachments().stream().map(AttachmentDTO::new).collect(Collectors.toSet())
                : Collections.emptySet();
        dueDate = task.getDueDate();
        createdAt = task.getCreatedAt();
        updatedAt = task.getUpdatedAt();
    }
}
