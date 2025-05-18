package com.pfc.octobets.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpcionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String descripcion;
    private Double totalApostado;
    private Double cuota;
    private Boolean ganadora;
}