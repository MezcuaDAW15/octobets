package com.pfc.octobets.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.OpcionDTO;
import com.pfc.octobets.model.mapper.OpcionMapper;
import com.pfc.octobets.repository.dao.OpcionRepository;
import com.pfc.octobets.repository.dao.TicketRepository;
import com.pfc.octobets.repository.entity.Opcion;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpcionServiceImpl implements OpcionService {

    @Autowired
    private OpcionRepository opcionRepository;

    @Autowired
    private OpcionMapper opcionMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public List<OpcionDTO> findAllByApuestaId(Long idApuesta) {
        log.info("Búsqueda de todas las opciones de la apuesta con id={}", idApuesta);
        return opcionRepository.findAllByIdApuesta(idApuesta).stream()
                .map(opcionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OpcionDTO findById(Long idApuesta, Long idOpcion) {
        log.info("Búsqueda de opción con id={} de la apuesta con id={}", idOpcion, idApuesta);
        return opcionRepository.findById(idOpcion)
                .filter(opcion -> opcion.getApuesta().getId().equals(idApuesta))
                .map(opcionMapper::toDTO)
                .orElseThrow(() -> {
                    String msg = "Opción no encontrada con id=" + idOpcion + " de la apuesta con id=" + idApuesta;
                    log.warn(msg);
                    return new ResourceNotFoundException(msg);
                });
    }

    @Override
    public void incrementarTotalApostado(Long opcionId, double cantidad) {

        Opcion opcion = opcionRepository.findById(opcionId)
                .orElseThrow(() -> {
                    String msg = "Opción no encontrada con id=" + opcionId;
                    log.warn(msg);
                    return new ResourceNotFoundException(msg);
                });
        opcion.setTotalApostado(opcion.getTotalApostado() + cantidad);
        opcionRepository.save(opcion);
    }

    @Override
    public void recalcularCuota(Long idApuesta) {

        List<Opcion> opciones = opcionRepository.findAllByIdApuesta(idApuesta);
        double totalApostado = opciones.stream()
                .mapToDouble(Opcion::getTotalApostado)
                .sum();
        if (totalApostado == 0) {
            log.warn("No se puede recalcular la cuota de la apuesta con id={} porque no hay apuestas", idApuesta);
            return;
        }
        for (Opcion opcion : opciones) {
            double cuota = totalApostado / opcion.getTotalApostado();
            opcion.setCuota(cuota);
            ticketRepository.findByOpcionId(opcion.getId()).forEach(ticket -> {
                ticket.setCuota(cuota);
                this.ticketRepository.save(ticket);
            });
            opcionRepository.save(opcion);
        }
    }

}