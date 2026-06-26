package com.dexcode.taskmasterai.dto.attachment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAttachmentDTO {
    @NotNull
    @NotBlank
    private String url;

    @NotNull
    @NotBlank
    private String fileType;

    private String caption;
}
