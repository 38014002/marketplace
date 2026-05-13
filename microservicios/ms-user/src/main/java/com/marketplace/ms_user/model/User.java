package com.marketplace.ms_user.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // Importante
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore // Con esto, el password nunca saldrá en los JSON de respuesta
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role;
}