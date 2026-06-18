package com.dexcode.taskmasterai.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachment extends Attachment{

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

}
