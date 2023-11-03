package com.rabin.securityproject.dto;


import com.rabin.securityproject.entity.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private int id;
    private String username;
    private String email;
    private String password;
    private Role roles;
}
