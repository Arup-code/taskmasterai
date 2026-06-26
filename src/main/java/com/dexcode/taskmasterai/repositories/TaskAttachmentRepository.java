package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
}
