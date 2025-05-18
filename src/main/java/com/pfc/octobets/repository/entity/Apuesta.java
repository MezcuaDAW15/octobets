package com.pfc.octobets.repository.entity;

import java.time.LocalDateTime;

import com.pfc.octobets.model.enums.EstadoApuesta;
import com.pfc.octobets.model.enums.TipoApuesta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "apuesta")
@AllArgsConstructor
@NoArgsConstructor
public class Apuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoApuesta estado;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoApuesta tipo;

    @ManyToOne
    @JoinColumn(name = "id_creador", nullable = false)
    private Usuario creador;
}
