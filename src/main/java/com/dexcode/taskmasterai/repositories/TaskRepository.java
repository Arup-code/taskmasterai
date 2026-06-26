package com.dexcode.taskmasterai.repositories;

import com.dexcode.taskmasterai.entities.Task;
import com.dexcode.taskmasterai.entities.Team;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByTeam(Team team, Pageable pageable);
    Page<Task> findByAssignee(User assignee, Pageable pageable);
    Page<Task> findByCreator(User creator, Pageable pageable);
    Page<Task> findByTeamAndStatus(Team team, TaskStatus status, Pageable pageable);
    Page<Task> findByAssigneeAndStatus(User assignee, TaskStatus status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.team = :team AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Task> searchByTeam(@Param("team") Team team, @Param("query") String query, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Task> searchByAssignee(@Param("assignee") User assignee, @Param("query") String query, Pageable pageable);
}
