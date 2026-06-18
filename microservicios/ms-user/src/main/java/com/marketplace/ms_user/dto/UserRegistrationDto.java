package com.marketplace.ms_user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {
    private String username;
    private String password;
    private String email;
    private String role;

    public String getUsername() {
        return username;
    }
}