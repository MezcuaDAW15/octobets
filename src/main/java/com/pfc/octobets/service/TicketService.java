package com.pfc.octobets.service;

import java.util.List;

import com.pfc.octobets.model.dto.TicketDTO;

public interface TicketService {

    void pagarGanador(Long id);

    TicketDTO crearTicket(TicketDTO ticketDTO);

    List<TicketDTO> getTicketsByIdUsuario(Long idUsuario);

    void devolverTickets(Long idApuesta);

}