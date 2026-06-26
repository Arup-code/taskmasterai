package com.dexcode.taskmasterai.dto;

import com.dexcode.taskmasterai.dto.attachment.AttachmentDTO;
import com.dexcode.taskmasterai.dto.user.UserDTO;
import com.dexcode.taskmasterai.entities.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CommentDTO {

    private Long id;

    private String content;

    private UserDTO user;

    private Set<AttachmentDTO> attachments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CommentDTO(Comment comment) {
        id = comment.getId();
        content = comment.getContent();
        user = comment.getUser() != null ? new UserDTO(comment.getUser()) : null;
        attachments = comment.getAttachments() != null
                ? comment.getAttachments().stream().map(AttachmentDTO::new).collect(Collectors.toSet())
                : Collections.emptySet();
        createdAt = comment.getCreatedAt();
        updatedAt = comment.getUpdatedAt();
    }
}