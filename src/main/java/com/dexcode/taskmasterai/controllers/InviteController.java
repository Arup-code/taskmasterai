package com.dexcode.taskmasterai.controllers;

import com.dexcode.taskmasterai.dto.invite.AcceptInviteDTO;
import com.dexcode.taskmasterai.dto.invite.CreateInviteDTO;
import com.dexcode.taskmasterai.dto.invite.InviteDTO;
import com.dexcode.taskmasterai.services.InviteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
public class InviteController {

    @Autowired
    private InviteService inviteService;

    @PostMapping("/teams/{teamId}/invites")
    public ResponseEntity<InviteDTO> createInvite(Principal principal,
                                                   @PathVariable Long teamId,
                                                   @RequestBody @Valid CreateInviteDTO dto) {
        InviteDTO invite = inviteService.createInvite(principal.getName(), teamId, dto);
        return ResponseEntity.status(201).body(invite);
    }

    @GetMapping("/teams/{teamId}/invites")
    public ResponseEntity<Page<InviteDTO>> getTeamInvites(Principal principal,
                                                           @PathVariable Long teamId,
                                                           Pageable pageable) {
        Page<InviteDTO> invites = inviteService.getTeamInvites(principal.getName(), teamId, pageable);
        return ResponseEntity.ok(invites);
    }

    @DeleteMapping("/invites/{inviteId}")
    public ResponseEntity<Map<String, String>> cancelInvite(Principal principal,
                                                             @PathVariable Long inviteId) {
        inviteService.cancelInvite(principal.getName(), inviteId);
        return ResponseEntity.ok(Map.of("message", "Invite cancelled successfully"));
    }

    @PostMapping("/invites/accept")
    public ResponseEntity<InviteDTO> acceptInvite(Principal principal,
                                                   @RequestBody @Valid AcceptInviteDTO dto) {
        InviteDTO invite = inviteService.acceptInvite(principal.getName(), dto.getToken());
        return ResponseEntity.ok(invite);
    }

    @GetMapping("/invites")
    public ResponseEntity<Page<InviteDTO>> getUserInvites(Principal principal, Pageable pageable) {
        Page<InviteDTO> invites = inviteService.getUserInvites(principal.getName(), pageable);
        return ResponseEntity.ok(invites);
    }
}
