package com.pfc.octobets.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "cartera")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "usuario")
public class Cartera {

    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "saldo_fichas", nullable = false)
    private Double saldoFichas;

}
