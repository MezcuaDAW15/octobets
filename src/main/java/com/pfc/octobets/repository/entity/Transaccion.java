package com.pfc.octobets.repository.entity;

import java.time.LocalDateTime;

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
@Table(name = "transaccion")
@AllArgsConstructor
@NoArgsConstructor
public class Transaccion {

    public enum Tipo {
        DEPOSITO, RETIRO
    }

    public enum Estado {
        PENDING, SUCCEEDED, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cantidad_dinero", nullable = false)
    private Double cantidadDinero;

    @Column(name = "cantidad_fichas", nullable = false)
    private Double cantidadFichas;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Column(name = "stripe_id")
    private String stripeId;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "id_cartera", nullable = false)
    private Cartera cartera;
}
