package com.pfc.octobets.repository.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "usuario")
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 45)
    private String avatar;

    @Column(name = "password_hash", nullable = false, length = 256)
    private String passwordHash;

    @OneToOne(mappedBy = "usuario", cascade = { CascadeType.PERSIST,
            CascadeType.MERGE }, fetch = FetchType.LAZY, orphanRemoval = true)
    private Cartera cartera;

    private Boolean enabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}
