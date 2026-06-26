package com.dexcode.taskmasterai.dto.attachment;

import com.dexcode.taskmasterai.entities.Attachment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class AttachmentDTO {

    private Long id;

    @NotNull
    @NotBlank
    private String url;

    @NotNull
    @NotBlank
    private String fileType;

    private String caption;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public AttachmentDTO(Attachment attachment) {
        id = attachment.getId();
        url = attachment.getUrl();
        fileType = attachment.getFileType();
        caption = attachment.getCaption();
        createdAt = attachment.getCreatedAt();
        updatedAt = attachment.getUpdatedAt();
    }
}
