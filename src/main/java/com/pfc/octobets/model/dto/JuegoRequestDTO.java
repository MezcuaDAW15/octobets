package com.pfc.octobets.model.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JuegoRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private UsuarioDTO usuarioDTO;
    private JuegoDTO juegoDTO;
    private Double cantidadApostada;
    private Map<String, Object> datosJugada;
}
