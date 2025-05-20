package com.pfc.octobets.service;

import java.util.List;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.model.dto.TicketDTO;

public interface ApuestaService {
    List<ApuestaDTO> findAll();

    ApuestaDTO findById(Long id);

    ApuestaDTO crearApuesta(ApuestaDTO apuestaDTO, Long idUsuario);

    ApuestaDTO actualizarApuesta(Long id, ApuestaDTO apuestaDTO);

    ApuestaDTO cerrarApuesta(Long id);

    ApuestaDTO resolverApuesta(Long id, Long idOpcionGanadora);

    void realizarApuesta(TicketDTO ticketDTO, Long id, Long opcionId);

    ApuestaDTO cancelarApuesta(Long id, String motivo);

    List<ApuestaDTO> findByUsuario(Long idUsuario);

}