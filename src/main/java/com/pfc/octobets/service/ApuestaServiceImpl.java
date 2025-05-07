package com.pfc.octobets.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.model.enums.EstadoApuesta;
import com.pfc.octobets.model.mapper.ApuestaMapper;
import com.pfc.octobets.model.mapper.UsuarioMapper;
import com.pfc.octobets.repository.dao.ApuestaRepository;
import com.pfc.octobets.repository.dao.OpcionRepository;
import com.pfc.octobets.repository.entity.Apuesta;
import com.pfc.octobets.repository.entity.Opcion;
import com.pfc.octobets.repository.entity.Usuario;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApuestaServiceImpl implements ApuestaService {

    @Autowired
    private ApuestaRepository apuestaRepository;
    @Autowired
    private ApuestaMapper apuestaMapper;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private OpcionRepository opcionRepository;
    @Autowired
    private TicketService ticketService;

    @Override
    public List<ApuestaDTO> findAll() {
        log.info("Búsqueda de todas las apuestas en el repositorio.");
        return apuestaRepository.findAll().stream()
                .map(apuestaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ApuestaDTO findById(Long id) {
        log.info("Búsqueda de apuesta con id={}", id);
        return apuestaRepository.findById(id)
                .map(apuestaMapper::toDTO)
                .orElseThrow(() -> {
                    String msg = "Apuesta no encontrada con id=" + id;
                    log.warn(msg);
                    return new ResourceNotFoundException(msg);
                });
    }

    @Override
    public ApuestaDTO crearApuesta(ApuestaDTO apuestaDTO) {
        log.info("Creando nueva apuesta: {}", apuestaDTO);
        apuestaDTO.setEstado(EstadoApuesta.ABIERTA);
        Apuesta entidad = apuestaMapper.toEntity(apuestaDTO);

        Apuesta guardada = apuestaRepository.save(entidad);
        log.info("Apuesta creada con id={}", guardada);

        return apuestaMapper.toDTO(guardada);

    }

    @Override
    public ApuestaDTO actualizarApuesta(Long id, ApuestaDTO apuestaDTO) {
        log.info("Actualizando apuesta id={} con datos {}", id, apuestaDTO);

        Apuesta existente = apuestaRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = "Imposible actualizar: apuesta no encontrada con id=" + id;
                    log.warn(msg);
                    return new ResourceNotFoundException(msg);
                });

        if (apuestaDTO.getTitulo() != null) {
            existente.setTitulo(apuestaDTO.getTitulo());
        }
        if (apuestaDTO.getDescripcion() != null) {
            existente.setDescripcion(apuestaDTO.getDescripcion());
        }
        if (apuestaDTO.getFechaCreacion() != null) {
            existente.setFechaCreacion(apuestaDTO.getFechaCreacion());
        }
        if (apuestaDTO.getFechaCierre() != null) {
            existente.setFechaCierre(apuestaDTO.getFechaCierre());
        }
        if (apuestaDTO.getEstado() != null) {
            existente.setEstado(apuestaDTO.getEstado());
        }

        Apuesta guardada = apuestaRepository.save(existente);
        log.info("Apuesta id={} actualizada.", id);
        return apuestaMapper.toDTO(guardada);
    }

    @Override
    public ApuestaDTO cerrarApuesta(Long id) {
        log.info("Cerrando apuesta id={}", id);
        Apuesta apuesta = apuestaRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = "Imposible cerrar: apuesta no encontrada con id=" + id;
                    log.warn(msg);
                    return new ResourceNotFoundException(msg);
                });

        apuesta.setEstado(EstadoApuesta.CERRADA);
        Apuesta guardada = apuestaRepository.save(apuesta);
        log.info("Apuesta id={} cerrada.", id);
        return apuestaMapper.toDTO(guardada);
    }

    @Override
    public ApuestaDTO resolverApuesta(Long id) {
        log.info("Resolviendo apuesta id={}", id);
        Apuesta apuesta = apuestaRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = "Imposible resolver: apuesta no encontrada con id=" + id;
                    log.warn(msg);
                    return new ResourceNotFoundException(msg);
                });

        List<Opcion> opciones = opcionRepository.findAllByIdApuesta(id);
        Opcion opcionGanadora = opciones.stream()
                .filter(Opcion::getGanadora)
                .findFirst()
                .orElseThrow(() -> {
                    String msg = "Imposible resolver: no hay opción ganadora para la apuesta con id=" + id;
                    log.warn(msg);
                    return new IllegalStateException(msg);
                });
        ticketService.pagarGanador(opcionGanadora.getId(), opcionGanadora.getCuota());
        apuesta.setEstado(EstadoApuesta.RSUELTA);
        apuestaRepository.save(apuesta);

        return apuestaMapper.toDTO(apuesta);
    }

}