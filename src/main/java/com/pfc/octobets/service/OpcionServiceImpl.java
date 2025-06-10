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

import jakarta.transaction.Transactional;

import com.pfc.octobets.common.ApiException;
import com.pfc.octobets.common.ErrorCode;
import java.util.Map;
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
                .orElseThrow(() -> new ApiException(
                    ErrorCode.BET_NOT_FOUND,
                    "Opción no encontrada",
                    Map.of("opcionId", idOpcion, "apuestaId", idApuesta)
                ));
    }

    @Override
    public void incrementarTotalApostado(Long opcionId, double cantidad) {

        Opcion opcion = opcionRepository.findById(opcionId)
                .orElseThrow(() -> new ApiException(
                    ErrorCode.BET_NOT_FOUND,
                    "Opción no encontrada",
                    Map.of("id", opcionId)
                ));
        opcion.setTotalApostado(opcion.getTotalApostado() + cantidad);
        opcionRepository.save(opcion);
    }

    @Override
    public void recalcularCuota(Long idApuesta) {

        List<Opcion> opciones = opcionRepository.findAllByIdApuesta(idApuesta);
        for (Opcion opcion : opciones) {

            log.warn("La opción con id={} tiene un total apostado: {}", opcion.getId(),
                    opcion.getTotalApostado());

        }
        double totalApostado = opciones.stream()
                .mapToDouble(Opcion::getTotalApostado)
                .sum();
        if (totalApostado == 0) {
            log.warn("No se puede recalcular la cuota de la apuesta con id={} porque no hay apuestas", idApuesta);
            opciones.forEach(opcion -> {
                opcion.setCuota(2.0);
                opcionRepository.save(opcion);
            });
            return;
        }
        for (Opcion opcion : opciones) {
            double cuota;
            if (opcion.getTotalApostado() == 0) {
                log.warn("TOTAL APOSTADO ES 0", opcion.getId());
                cuota = 1.0;
            } else {
                cuota = totalApostado / opcion.getTotalApostado();

            }
            opcion.setCuota(cuota);
            ticketRepository.findByOpcionId(opcion.getId()).forEach(ticket -> {
                ticket.setCuota(cuota);
                this.ticketRepository.save(ticket);
            });
            opcionRepository.save(opcion);
        }
    }

    @Override
    @Transactional
    public Opcion setOpcionGanadora(Long idOpcionGanadora) {
        log.info("Marcando opción ganadora con id={}", idOpcionGanadora);
        Opcion opcion = opcionRepository.findById(idOpcionGanadora)
                .orElseThrow(() -> new ApiException(
                    ErrorCode.BET_NOT_FOUND,
                    "Opción no encontrada",
                    Map.of("id", idOpcionGanadora)
                ));
        opcion.setGanadora(true);
        return opcionRepository.save(opcion);
    }

}