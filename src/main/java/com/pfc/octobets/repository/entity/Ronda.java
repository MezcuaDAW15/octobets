package com.pfc.octobets.repository.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ronda")
@AllArgsConstructor
@NoArgsConstructor
public class Ronda {

    @Id
    private Long id;

    @Column(name = "fecha_jugada", nullable = false)
    private LocalDateTime fechaJugada;

    @ManyToOne
    @JoinColumn(name = "id_juego", nullable = false)
    private Juego juego;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "cantidad_fichas", nullable = false)
    private Double cantidadFichas;

    @Column(nullable = false)
    private Double cuota;

    @Column(name = "es_ganadora", nullable = false)
    private Boolean esGanadora;
}
