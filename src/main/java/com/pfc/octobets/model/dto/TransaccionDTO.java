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
    private Double cantidadDinero;
    private Double cantidadFichas;
    private LocalDateTime fecha;
    private CarteraDTO carteraDTO;
}
