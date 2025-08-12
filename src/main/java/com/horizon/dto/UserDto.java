package com.horizon.dto;

import com.horizon.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String username;
    private String password;
    private String image;
    private Role role;

    public UserDto(Long id,String email, String username, String password, String image, Role role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.image = image;
        this.role = role;
    }
}