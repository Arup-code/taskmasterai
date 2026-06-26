package com.dexcode.taskmasterai.dto.TeamMember;

import com.dexcode.taskmasterai.dto.user.UserDTO;
import com.dexcode.taskmasterai.entities.TeamMember;
import com.dexcode.taskmasterai.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDTO {
    private UserDTO user;
    private Role role;
    private LocalDateTime joinedAt;

    public TeamMemberDTO(TeamMember teamMember) {
        this.user = new UserDTO(teamMember.getUser());
        this.role = teamMember.getRole();
        this.joinedAt = teamMember.getJoinedAt();
    }
}
