package com.pfc.octobets.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.TicketDTO;
import com.pfc.octobets.model.mapper.TicketMapper;
import com.pfc.octobets.repository.dao.CarteraRepository;
import com.pfc.octobets.repository.dao.OpcionRepository;
import com.pfc.octobets.repository.dao.TicketRepository;
import com.pfc.octobets.repository.entity.Cartera;
import com.pfc.octobets.repository.entity.Ticket;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final OpcionRepository opcionRepository;

    @Autowired
    private CarteraRepository carteraRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private OpcionService opcionService;
    @Autowired
    private CarteraService carteraService;

    TicketServiceImpl(OpcionRepository opcionRepository) {
        this.opcionRepository = opcionRepository;
    }

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

    @Override
    public TicketDTO crearTicket(TicketDTO dto) {
        Long idUsuario = dto.getUsuario().getId();
        double fichas = dto.getCantidadFichas();

        double saldo = carteraService.getSaldo(idUsuario);
        if (saldo < fichas) {
            log.error("El usuario con id={} no tiene saldo suficiente para crear el ticket", idUsuario);
            throw new IllegalArgumentException("No tienes saldo suficiente");
        }

        Ticket ticket = ticketMapper.toEntity(dto);
        ticket.setFecha(LocalDateTime.now());

        ticket = ticketRepository.save(ticket);
        log.info("Se ha creado un ticket con id={} para el usuario con id={}", ticket);

        carteraService.cobrar(idUsuario, fichas);

        opcionService.incrementarTotalApostado(ticket.getOpcion().getId(), fichas);
        opcionService.recalcularCuota(opcionRepository.findById(ticket.getOpcion().getId()).get().getApuesta().getId());

        return ticketMapper.toDTO(ticket);
    }

    @Override
    public List<TicketDTO> getTicketsByIdUsuario(Long idUsuario) {
        log.info("Búsqueda de tickets para el usuario con id={}", idUsuario);
        return ticketRepository.findByUsuarioId(idUsuario).stream()
                .map(ticketMapper::toDTO)
                .toList();
    }

}