package com.pfc.octobets.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.repository.dao.CarteraRepository;
import com.pfc.octobets.repository.dao.TicketRepository;
import com.pfc.octobets.repository.entity.Cartera;
import com.pfc.octobets.repository.entity.Ticket;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    @Autowired
    private CarteraRepository carteraRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public void pagarGanador(Long idOpcion, Double cuota) {
        log.info("Pago de ganador para la opción con id={}", idOpcion);
        List<Ticket> tickets = ticketRepository.findByOpcionId(idOpcion);
        tickets.forEach(ticket -> {
            Double cantidad = ticket.getCantidadFichas() * cuota;
            Long idUsuario = ticket.getUsuario().getId();
            Double saldoActual = carteraRepository.findById(idUsuario).get().getSaldoFichas();
            Double nuevoSaldo = saldoActual + cantidad;
            Cartera cartera = carteraRepository.findById(idUsuario).get();
            cartera.setSaldoFichas(nuevoSaldo);
            carteraRepository.save(cartera);
            log.info("Se ha pagado {} a la cartera del usuario con id={}", cantidad, idUsuario);
        });
    }

}