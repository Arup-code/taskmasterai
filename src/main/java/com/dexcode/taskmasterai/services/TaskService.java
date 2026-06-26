package com.dexcode.taskmasterai.services;

import com.dexcode.taskmasterai.dto.CommentDTO;
import com.dexcode.taskmasterai.dto.CreateCommentDTO;
import com.dexcode.taskmasterai.dto.attachment.AttachmentDTO;
import com.dexcode.taskmasterai.dto.attachment.CreateAttachmentDTO;
import com.dexcode.taskmasterai.dto.task.CreateTaskDTO;
import com.dexcode.taskmasterai.dto.task.TaskDTO;
import com.dexcode.taskmasterai.dto.task.UpdateTaskDTO;
import com.dexcode.taskmasterai.entities.*;
import com.dexcode.taskmasterai.enums.Priority;
import com.dexcode.taskmasterai.enums.TaskStatus;
import com.dexcode.taskmasterai.exceptions.task.TaskAccessDeniedException;
import com.dexcode.taskmasterai.exceptions.task.TaskNotFoundException;
import com.dexcode.taskmasterai.exceptions.team.TeamNotFoundException;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    // ─── Task CRUD ────────────────────────────────────────────

    @Transactional
    public TaskDTO createTask(String email, Long teamId, CreateTaskDTO dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new TaskAccessDeniedException("You are not a member of this team"));

        User assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new UserNotFoundException("Assignee not found"));
            teamMemberRepository.findByUserAndTeam(assignee, team)
                    .orElseThrow(() -> new TaskAccessDeniedException("Assignee is not a member of this team"));
        }

        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(TaskStatus.OPEN)
                .priority(dto.getPriority() != null ? dto.getPriority() : Priority.MEDIUM)
                .creator(user)
                .assignee(assignee)
                .team(team)
                .dueDate(dto.getDueDate())
                .createdAt(LocalDateTime.now())
                .build();

        taskRepository.save(task);
        return new TaskDTO(task);
    }

    public TaskDTO getTaskById(String email, Long taskId) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        verifyTeamMembership(user, task.getTeam());

        return new TaskDTO(task);
    }

    public Page<TaskDTO> getTeamTasks(String email, Long teamId, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        verifyTeamMembership(user, team);

        return taskRepository.findByTeam(team, pageable).map(TaskDTO::new);
    }

    public Page<TaskDTO> getMyTasks(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        return taskRepository.findByAssignee(user, pageable).map(TaskDTO::new);
    }

    @Transactional
    public TaskDTO updateTask(String email, Long taskId, UpdateTaskDTO dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        verifyTeamMembership(user, task.getTeam());

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new UserNotFoundException("Assignee not found"));
            verifyTeamMembership(assignee, task.getTeam());
            task.setAssignee(assignee);
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);

        return new TaskDTO(task);
    }

    @Transactional
    public void deleteTask(String email, Long taskId) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        verifyTeamMembership(user, task.getTeam());

        taskRepository.delete(task);
    }

    // ─── Filtering & Searching ────────────────────────────────

    public Page<TaskDTO> getTeamTasksByStatus(String email, Long teamId, TaskStatus status, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        verifyTeamMembership(user, team);

        return taskRepository.findByTeamAndStatus(team, status, pageable).map(TaskDTO::new);
    }

    public Page<TaskDTO> getMyTasksByStatus(String email, TaskStatus status, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        return taskRepository.findByAssigneeAndStatus(user, status, pageable).map(TaskDTO::new);
    }

    public Page<TaskDTO> searchTeamTasks(String email, Long teamId, String query, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        verifyTeamMembership(user, team);

        return taskRepository.searchByTeam(team, query, pageable).map(TaskDTO::new);
    }

    public Page<TaskDTO> searchMyTasks(String email, String query, Pageable pageable) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        return taskRepository.searchByAssignee(user, query, pageable).map(TaskDTO::new);
    }

    // ─── Comments ─────────────────────────────────────────────

    @Transactional
    public CommentDTO addComment(String email, Long taskId, CreateCommentDTO dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        verifyTeamMembership(user, task.getTeam());

        Comment comment = Comment.builder()
                .task(task)
                .user(user)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
        return new CommentDTO(comment);
    }

    public List<CommentDTO> getTaskComments(String email, Long taskId) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        verifyTeamMembership(user, task.getTeam());

        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(CommentDTO::new)
                .toList();
    }

    // ─── Attachments ──────────────────────────────────────────

    @Transactional
    public AttachmentDTO addAttachment(String email, Long taskId, CreateAttachmentDTO dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        verifyTeamMembership(user, task.getTeam());

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setUrl(dto.getUrl());
        attachment.setFileType(dto.getFileType());
        attachment.setCaption(dto.getCaption());
        attachment.setCreatedAt(LocalDateTime.now());

        taskAttachmentRepository.save(attachment);
        return new AttachmentDTO(attachment);
    }

    @Transactional
    public void deleteAttachment(String email, Long taskId, Long attachmentId) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        verifyTeamMembership(user, task.getTeam());

        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new TaskNotFoundException("Attachment not found"));

        if (!attachment.getTask().getId().equals(taskId)) {
            throw new TaskNotFoundException("Attachment does not belong to this task");
        }

        taskAttachmentRepository.delete(attachment);
    }

    // ─── Helper ───────────────────────────────────────────────

    private void verifyTeamMembership(User user, Team team) {
        teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new TaskAccessDeniedException("You are not a member of this team"));
    }
}
