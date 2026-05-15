package com.marketplace.ms_user.model;

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
    private Integer id; // 🚩 Fíjate: Es Integer

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // 🚩 Sin anotaciones de Jackson para que viaje siempre

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role;
}