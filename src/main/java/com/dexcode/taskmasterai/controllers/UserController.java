package com.dexcode.taskmasterai.controllers;

import com.dexcode.taskmasterai.dto.user.ChangePasswordDTO;
import com.dexcode.taskmasterai.dto.user.UpdateUserDTO;
import com.dexcode.taskmasterai.dto.user.UserDTO;
import com.dexcode.taskmasterai.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        UserDTO user = userService.getProfile(principal.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/me")
    public ResponseEntity<UserDTO> updateProfile(Principal principal,
                                                  @RequestBody @Valid UpdateUserDTO dto) {
        UserDTO user = userService.updateProfile(principal.getName(), dto);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/me/password")
    public ResponseEntity<Map<String, String>> changePassword(Principal principal,
                                                               @RequestBody @Valid ChangePasswordDTO dto) {
        userService.changePassword(principal.getName(), dto);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<UserDTO>> searchUsers(@RequestParam String q, Pageable pageable) {
        Page<UserDTO> users = userService.searchUsers(q, pageable);
        return ResponseEntity.ok(users);
    }
}
