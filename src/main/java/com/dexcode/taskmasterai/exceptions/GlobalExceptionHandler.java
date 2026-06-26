package com.dexcode.taskmasterai.exceptions;

import com.dexcode.taskmasterai.exceptions.invite.InviteAlreadyAcceptedException;
import com.dexcode.taskmasterai.exceptions.invite.InviteEmailMismatchException;
import com.dexcode.taskmasterai.exceptions.invite.InviteNotFoundException;
import com.dexcode.taskmasterai.exceptions.team.InsufficientPriviledgeTeamException;
import com.dexcode.taskmasterai.exceptions.team.MemberNotFoundException;
import com.dexcode.taskmasterai.exceptions.team.TeamNotFoundException;
import com.dexcode.taskmasterai.exceptions.user.UserNotFoundException;
import com.dexcode.taskmasterai.exceptions.user.InvalidPasswordException;
import com.dexcode.taskmasterai.exceptions.task.TaskAccessDeniedException;
import com.dexcode.taskmasterai.exceptions.task.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTeamNotFound(TeamNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMemberNotFound(MemberNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InviteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInviteNotFound(InviteNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InsufficientPriviledgeTeamException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientPrivilege(InsufficientPriviledgeTeamException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InviteAlreadyAcceptedException.class)
    public ResponseEntity<Map<String, Object>> handleInviteAlreadyAccepted(InviteAlreadyAcceptedException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InviteEmailMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleInviteEmailMismatch(InviteEmailMismatchException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFound(TaskNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TaskAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleTaskAccessDenied(TaskAccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPassword(InvalidPasswordException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
