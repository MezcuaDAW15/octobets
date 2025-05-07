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
public class JuegoResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean resultado;
    private Integer fichasGanadas;
    private Map<String, Object> infoVisual;
}
