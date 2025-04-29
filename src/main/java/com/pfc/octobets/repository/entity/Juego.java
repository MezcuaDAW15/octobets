package com.pfc.octobets.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
@AllArgsConstructor
public class Juego {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;
}
