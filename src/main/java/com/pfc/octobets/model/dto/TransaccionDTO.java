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
public class TransaccionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private LocalDateTime fecha;
    private String tipo;
    private Double cantidadFichas;
    private Double cantidadDinero;
    private String stripeId;
    private String estado;
}
