package com.marketplace.ms_auth.model;

import lombok.Data;

@Data
public class Usuario {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String role;
}