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

@Data
@Entity
@Table(name = "usuario")
@AllArgsConstructor
public class Transaccion {

    @Id
    private Long id;

    @Column(name = "cantidad_dinero", nullable = false)
    private Double cantidadDinero;

    @Column(name = "cantidad_fichas", nullable = false)
    private Double cantidadFichas;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "id_cartera", nullable = false)
    private Cartera cartera;
}
