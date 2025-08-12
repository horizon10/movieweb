package com.horizon.dto;

import com.horizon.entity.Role;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    private Role role;
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}