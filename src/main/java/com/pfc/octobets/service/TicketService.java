package com.pfc.octobets.service;

import java.util.List;

import com.pfc.octobets.model.dto.TicketDTO;

public interface TicketService {

    void pagarGanador(Long id, Double cuota);

    TicketDTO crearTicket(TicketDTO ticketDTO);

    List<TicketDTO> getTicketsByIdUsuario(Long idUsuario);

}