package com.pfc.octobets.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.OpcionDTO;
import com.pfc.octobets.model.mapper.OpcionMapper;
import com.pfc.octobets.repository.dao.OpcionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpcionServiceImpl implements OpcionService {

    @Autowired
    private OpcionRepository opcionRepository;

    @Autowired
    private OpcionMapper opcionMapper;

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

}