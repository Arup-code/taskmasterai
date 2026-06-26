package com.dexcode.taskmasterai.dto.invite;

import com.dexcode.taskmasterai.dto.user.UserDTO;
import com.dexcode.taskmasterai.entities.Invite;
import com.dexcode.taskmasterai.enums.InviteStatus;
import com.dexcode.taskmasterai.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InviteDTO {

    private Long id;

    private UserDTO userDTO;

    private String inviteEmail;

    private String token;

    private InviteStatus status;

    private Role role;

    private LocalDateTime createdAt;

    public InviteDTO(Invite invite) {
        id = invite.getId();
        userDTO = new UserDTO(invite.getUser());
        inviteEmail = invite.getInviteEmail();
        token = invite.getToken();
        status = invite.getStatus();
        role = invite.getRole();
        createdAt = invite.getCreatedAt();
    }
}
