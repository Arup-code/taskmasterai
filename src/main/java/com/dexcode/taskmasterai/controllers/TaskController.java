package com.dexcode.taskmasterai.controllers;

import com.dexcode.taskmasterai.dto.CommentDTO;
import com.dexcode.taskmasterai.dto.CreateCommentDTO;
import com.dexcode.taskmasterai.dto.attachment.AttachmentDTO;
import com.dexcode.taskmasterai.dto.attachment.CreateAttachmentDTO;
import com.dexcode.taskmasterai.dto.task.CreateTaskDTO;
import com.dexcode.taskmasterai.dto.task.TaskDTO;
import com.dexcode.taskmasterai.dto.task.UpdateTaskDTO;
import com.dexcode.taskmasterai.enums.TaskStatus;
import com.dexcode.taskmasterai.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    // ─── Task CRUD ────────────────────────────────────────────

    @PostMapping("/teams/{teamId}/tasks")
    public ResponseEntity<TaskDTO> createTask(Principal principal,
                                               @PathVariable Long teamId,
                                               @RequestBody @Valid CreateTaskDTO dto) {
        TaskDTO task = taskService.createTask(principal.getName(), teamId, dto);
        return ResponseEntity.status(201).body(task);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDTO> getTaskById(Principal principal, @PathVariable Long taskId) {
        TaskDTO task = taskService.getTaskById(principal.getName(), taskId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/teams/{teamId}/tasks")
    public ResponseEntity<Page<TaskDTO>> getTeamTasks(Principal principal,
                                                       @PathVariable Long teamId,
                                                       Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getTeamTasks(principal.getName(), teamId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/me")
    public ResponseEntity<Page<TaskDTO>> getMyTasks(Principal principal, Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getMyTasks(principal.getName(), pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(Principal principal,
                                               @PathVariable Long taskId,
                                               @RequestBody @Valid UpdateTaskDTO dto) {
        TaskDTO task = taskService.updateTask(principal.getName(), taskId, dto);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String, String>> deleteTask(Principal principal, @PathVariable Long taskId) {
        taskService.deleteTask(principal.getName(), taskId);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
    }

    // ─── Filtering & Searching ────────────────────────────────

    @GetMapping("/teams/{teamId}/tasks/filter")
    public ResponseEntity<Page<TaskDTO>> getTeamTasksByStatus(Principal principal,
                                                               @PathVariable Long teamId,
                                                               @RequestParam TaskStatus status,
                                                               Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getTeamTasksByStatus(principal.getName(), teamId, status, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/me/filter")
    public ResponseEntity<Page<TaskDTO>> getMyTasksByStatus(Principal principal,
                                                             @RequestParam TaskStatus status,
                                                             Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getMyTasksByStatus(principal.getName(), status, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/teams/{teamId}/tasks/search")
    public ResponseEntity<Page<TaskDTO>> searchTeamTasks(Principal principal,
                                                          @PathVariable Long teamId,
                                                          @RequestParam String q,
                                                          Pageable pageable) {
        Page<TaskDTO> tasks = taskService.searchTeamTasks(principal.getName(), teamId, q, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/me/search")
    public ResponseEntity<Page<TaskDTO>> searchMyTasks(Principal principal,
                                                        @RequestParam String q,
                                                        Pageable pageable) {
        Page<TaskDTO> tasks = taskService.searchMyTasks(principal.getName(), q, pageable);
        return ResponseEntity.ok(tasks);
    }

    // ─── Comments ─────────────────────────────────────────────

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentDTO> addComment(Principal principal,
                                                  @PathVariable Long taskId,
                                                  @RequestBody @Valid CreateCommentDTO dto) {
        CommentDTO comment = taskService.addComment(principal.getName(), taskId, dto);
        return ResponseEntity.status(201).body(comment);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentDTO>> getTaskComments(Principal principal,
                                                             @PathVariable Long taskId) {
        List<CommentDTO> comments = taskService.getTaskComments(principal.getName(), taskId);
        return ResponseEntity.ok(comments);
    }

    // ─── Attachments ──────────────────────────────────────────

    @PostMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<AttachmentDTO> addAttachment(Principal principal,
                                                        @PathVariable Long taskId,
                                                        @RequestBody @Valid CreateAttachmentDTO dto) {
        AttachmentDTO attachment = taskService.addAttachment(principal.getName(), taskId, dto);
        return ResponseEntity.status(201).body(attachment);
    }

    @DeleteMapping("/tasks/{taskId}/attachments/{attachmentId}")
    public ResponseEntity<Map<String, String>> deleteAttachment(Principal principal,
                                                                 @PathVariable Long taskId,
                                                                 @PathVariable Long attachmentId) {
        taskService.deleteAttachment(principal.getName(), taskId, attachmentId);
        return ResponseEntity.ok(Map.of("message", "Attachment deleted successfully"));
    }
}
