package com.pfc.octobets.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.common.ApiException;
import com.pfc.octobets.common.ErrorCode;
import com.pfc.octobets.model.dto.TicketDTO;
import com.pfc.octobets.model.enums.EstadoApuesta;
import com.pfc.octobets.model.mapper.TicketMapper;
import com.pfc.octobets.repository.dao.CarteraRepository;
import com.pfc.octobets.repository.dao.OpcionRepository;
import com.pfc.octobets.repository.dao.TicketRepository;
import com.pfc.octobets.repository.entity.Cartera;
import com.pfc.octobets.repository.entity.Opcion;
import com.pfc.octobets.repository.entity.Ticket;
import com.stripe.model.tax.Registration.CountryOptions.Es;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

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
    @Autowired
    private OpcionRepository opcionRepository;

    @Override
    public void pagarGanador(Long idOpcion) {
        log.info("Pago de ganador para la opción con id={}", idOpcion);
        List<Ticket> ticketsGanadores = ticketRepository.findByOpcionId(idOpcion);
        for (Ticket ticket : ticketsGanadores) {
            Cartera cartera = carteraRepository.findById(ticket.getUsuario().getId()).orElseThrow();
            double cantidadGanada = ticket.getCantidadFichas() * ticket.getOpcion().getCuota();
            cartera.setSaldoFichas(cartera.getSaldoFichas() + cantidadGanada);
            carteraRepository.save(cartera);
            log.info("Se ha pagado al usuario con id={} la cantidad de {} por el ticket con id={}",
                    ticket.getUsuario().getId(), cantidadGanada, ticket.getId());
        }
    }

    @Override
    public TicketDTO crearTicket(TicketDTO dto) {
        log.info("Creación de ticket para el usuario con id={} y cantidad de fichas={}", dto.getUsuario().getId(),
                dto.getCantidadFichas());
        Long idUsuario = dto.getUsuario().getId();
        double fichas = dto.getCantidadFichas();

        double saldo = carteraService.getSaldo(idUsuario);
        if (saldo < fichas) {
            log.error("El usuario con id={} no tiene saldo suficiente para crear el ticket", idUsuario);
            throw new ApiException(
                    ErrorCode.STAKE_LIMIT_REACHED,
                    "No tienes saldo suficiente",
                    Map.of("userId", idUsuario, "saldo", saldo, "cantidad", fichas));
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
                .filter(ticket -> !ticket.getOpcion().getApuesta().getEstado().equals(EstadoApuesta.ELIMINADA))
                .map(ticketMapper::toDTO)
                .toList();
    }

    @Override
    public void devolverTickets(Long idApuesta) {
        log.info("Devolviendo tickets de la apuesta con id={}", idApuesta);
        List<Opcion> opciones = opcionRepository.findAllByIdApuesta(idApuesta);
        for (Opcion opcion : opciones) {
            List<Ticket> tickets = ticketRepository.findByOpcionId(opcion.getId());
            for (Ticket ticket : tickets) {
                Cartera cartera = carteraRepository.findById(ticket.getUsuario().getId()).orElseThrow();
                cartera.setSaldoFichas(cartera.getSaldoFichas() + ticket.getCantidadFichas());
                carteraRepository.save(cartera);
                log.info("Se ha devuelto el ticket con id={} al usuario con id={}", ticket.getId(),
                        ticket.getUsuario().getId());
            }
        }
    }

}