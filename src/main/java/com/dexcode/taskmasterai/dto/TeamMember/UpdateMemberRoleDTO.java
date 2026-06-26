package com.dexcode.taskmasterai.dto.TeamMember;

import com.dexcode.taskmasterai.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMemberRoleDTO {
    @NotNull
    private Role role;
}
