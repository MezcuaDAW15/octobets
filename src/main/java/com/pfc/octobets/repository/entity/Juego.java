package com.pfc.octobets.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "juego")
@AllArgsConstructor
@NoArgsConstructor
public class Juego {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;
}
