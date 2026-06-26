package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId, Pageable pageable);
    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}
