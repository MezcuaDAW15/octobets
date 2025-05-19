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
public class TicketDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String apuestaTitulo;
    private String apuestaDescripcion;
    private TipoApuesta apuestaTipo;
    private EstadoApuesta apuestaEstado;
    private Double cantidadFichas;
    private Double cuota;
    private LocalDateTime fecha;
    private UsuarioDTO usuario;
    private OpcionDTO opcion;
}