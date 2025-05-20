package com.pfc.octobets.service;

import java.util.List;

import com.pfc.octobets.model.dto.OpcionDTO;
import com.pfc.octobets.repository.entity.Opcion;

public interface OpcionService {

    List<OpcionDTO> findAllByApuestaId(Long idApuesta);

    OpcionDTO findById(Long idApuesta, Long idOpcion);

    void incrementarTotalApostado(Long id, double fichas);

    void recalcularCuota(Long id);

    Opcion setOpcionGanadora(Long idOpcionGanadora);

}