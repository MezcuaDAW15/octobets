package com.pfc.octobets.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.pfc.octobets.model.enums.EstadoApuesta;
import com.pfc.octobets.model.enums.TipoApuesta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApuestaDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCierre;
    private EstadoApuesta estado;
    private TipoApuesta tipo;
}
