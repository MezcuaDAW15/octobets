package com.pfc.octobets.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RondaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private LocalDateTime fechaJugada;
    private JuegoDTO juego;
    private UsuarioDTO usuario;
    private Double cantidadFichas;
    private Double cuota;
    private Boolean esGanadora;
}