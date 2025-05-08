package com.pfc.octobets.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.model.dto.TicketDTO;
import com.pfc.octobets.model.enums.EstadoApuesta;
import com.pfc.octobets.model.mapper.ApuestaMapper;
import com.pfc.octobets.model.mapper.TicketMapper;
import com.pfc.octobets.repository.dao.ApuestaRepository;
import com.pfc.octobets.repository.dao.OpcionRepository;
import com.pfc.octobets.repository.dao.TicketRepository;
import com.pfc.octobets.repository.dao.UsuarioRepository;
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
    private OpcionRepository opcionRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TicketMapper ticketMapper;

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
    public void realizarApuesta(TicketDTO ticketDTO, Long idApuesta, Long idOpcion) {

        Apuesta apuesta = apuestaRepository.findById(idApuesta)
                .orElseThrow(() -> new IllegalArgumentException("Apuesta no encontrada"));

        if (apuesta.getEstado() != EstadoApuesta.ABIERTA) {
            throw new IllegalArgumentException("La apuesta no está abierta");
        }

        Opcion opcion = opcionRepository.findById(idOpcion)
                .orElseThrow(() -> new IllegalArgumentException("Opción no encontrada"));

        if (!opcion.getApuesta().getId().equals(idApuesta)) {
            throw new IllegalArgumentException("La opción no pertenece a la apuesta indicada");
        }

        usuarioRepository.findById(ticketDTO.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        opcion.setTotalApostado(opcion.getTotalApostado() + ticketDTO.getCantidadFichas());
        opcionRepository.save(opcion);

        ticketRepository.save(ticketMapper.toEntity(ticketDTO));
    }
}