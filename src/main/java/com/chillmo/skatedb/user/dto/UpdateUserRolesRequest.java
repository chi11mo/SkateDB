package com.chillmo.skatedb.user.dto;

import com.chillmo.skatedb.user.domain.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

/**
 * Admin payload to change the roles assigned to a user.
 */
@Data
public class UpdateUserRolesRequest {

    @NotEmpty
    private Set<Role> roles;
}
