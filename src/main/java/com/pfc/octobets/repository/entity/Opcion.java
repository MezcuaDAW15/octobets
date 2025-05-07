package com.pfc.octobets.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "opcion")
@AllArgsConstructor
@NoArgsConstructor
public class Opcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "total_apostado", nullable = false)
    private Double totalApostado;

    @Column
    private Double cuota;

    @Column
    private Boolean ganadora;

    @ManyToOne
    @JoinColumn(name = "id_apuesta", nullable = false)
    private Apuesta apuesta;
}
