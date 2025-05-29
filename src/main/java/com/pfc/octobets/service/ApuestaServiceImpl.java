package com.pfc.octobets.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.common.ApiException;
import com.pfc.octobets.common.ErrorCode;
import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.model.dto.TicketDTO;
import com.pfc.octobets.model.enums.EstadoApuesta;
import com.pfc.octobets.model.mapper.ApuestaMapper;
import com.pfc.octobets.model.mapper.OpcionMapper;
import com.pfc.octobets.model.mapper.TicketMapper;
import com.pfc.octobets.repository.dao.ApuestaRepository;
import com.pfc.octobets.repository.dao.OpcionRepository;
import com.pfc.octobets.repository.dao.TicketRepository;
import com.pfc.octobets.repository.dao.UsuarioRepository;
import com.pfc.octobets.repository.entity.Apuesta;
import com.pfc.octobets.repository.entity.Opcion;

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
    private OpcionService opcionService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private OpcionMapper opcionMapper;

    @Override
    public List<ApuestaDTO> findAll() {
        log.info("Búsqueda de todas las apuestas en el repositorio.");
        return apuestaRepository.findAllAbiertas().stream()
                .map(apuestaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ApuestaDTO findById(Long id) {
        log.info("Búsqueda de apuesta con id={}", id);
        return apuestaRepository.findById(id)
                .filter(apuesta -> apuesta.getEstado() != EstadoApuesta.ELIMINADA)
                .map(apuestaMapper::toDTO)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BET_NOT_FOUND,
                        "Apuesta no encontrada",
                        Map.of("id", id)));
    }

    @Override
    public ApuestaDTO crearApuesta(ApuestaDTO apuestaDTO, Long idUsuario) {
        log.info("Creando apuesta con datos {}", apuestaDTO);
        Apuesta apuesta = apuestaMapper.toEntity(apuestaDTO);
        apuesta.setCreador(usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado",
                        Map.of("id", idUsuario))));

        List<Opcion> opciones = apuestaDTO.getOpciones().stream()
                .map(opcionMapper::toEntity)
                .collect(Collectors.toList());
        apuestaDTO.setOpciones(new ArrayList<>());
        Apuesta guardada = apuestaRepository.save(apuesta);
        for (Opcion opcion : opciones) {
            opcion.setApuesta(guardada);
            guardada.getOpciones().add(opcion);
        }

        log.info("Apuesta creada con id={}", guardada.getId());
        return apuestaMapper.toDTO(guardada);

    }

    @Override
    public ApuestaDTO actualizarApuesta(Long id, ApuestaDTO apuestaDTO) {
        log.info("Actualizando apuesta id={} con datos {}", id, apuestaDTO);

        Apuesta existente = apuestaRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BET_NOT_FOUND,
                        "Apuesta no encontrada",
                        Map.of("id", id)));

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
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BET_NOT_FOUND,
                        "Apuesta no encontrada",
                        Map.of("id", id)));

        apuesta.setEstado(EstadoApuesta.CERRADA);
        apuesta.setFechaCierre(java.time.LocalDateTime.now());
        opcionService.recalcularCuota(id);
        Apuesta guardada = apuestaRepository.save(apuesta);
        log.info("Apuesta id={} cerrada.", id);
        return apuestaMapper.toDTO(guardada);
    }

    @Override
    public ApuestaDTO cancelarApuesta(Long id, String motivo) {
        log.info("Cancelando apuesta id={} por motivo: {}", id, motivo);
        Apuesta apuesta = apuestaRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BET_NOT_FOUND,
                        "Imposible cancelar: apuesta no encontrada",
                        Map.of("id", id)));

        apuesta.setEstado(EstadoApuesta.CANCELADA);
        String descripcion = apuesta.getDescripcion() + " Cancelado: " + motivo;
        apuesta.setDescripcion(descripcion);
        apuesta.setFechaCierre(java.time.LocalDateTime.now());
        Apuesta guardada = apuestaRepository.save(apuesta);
        ticketService.devolverTickets(guardada.getId());
        log.info("Apuesta id={} cancelada.", id);
        return apuestaMapper.toDTO(guardada);
    }

    @Override
    public ApuestaDTO resolverApuesta(Long id, Long idOpcionGanadora) {
        log.info("Resolviendo apuesta id={} con opción ganadora id={}", id, idOpcionGanadora);
        Apuesta apuesta = apuestaRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BET_NOT_FOUND,
                        "Imposible resolver: apuesta no encontrada",
                        Map.of("id", id)));
        if (apuesta.getEstado() != EstadoApuesta.CERRADA) {
            throw new ApiException(
                    ErrorCode.BET_ALREADY_PLACED,
                    "La apuesta no está cerrada",
                    Map.of("id", id));
        }
        Opcion opcionGanadora = opcionRepository.findById(idOpcionGanadora)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BET_NOT_FOUND,
                        "Opción ganadora no encontrada",
                        Map.of("id", idOpcionGanadora)));
        if (!opcionGanadora.getApuesta().getId().equals(id)) {
            String msg = "La opción ganadora con id=" + idOpcionGanadora + " no pertenece a la apuesta con id=" + id;
            log.error(msg);
            throw new ApiException(
                    ErrorCode.BET_NOT_FOUND,
                    "La opción no pertenece a la apuesta",
                    Map.of("apuestaId", id, "opcionId", idOpcionGanadora));
        }

        opcionGanadora = opcionService.setOpcionGanadora(idOpcionGanadora);
        ticketService.pagarGanador(opcionGanadora.getId());
        apuesta.setEstado(EstadoApuesta.RESUELTA);
        return apuestaMapper.toDTO(apuestaRepository.save(apuesta));

    }

    public void realizarApuesta(TicketDTO ticketDTO, Long idApuesta, Long idOpcion) {

        Apuesta apuesta = apuestaRepository.findById(idApuesta)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BET_NOT_FOUND,
                        "Apuesta no encontrada",
                        Map.of("id", idApuesta)));

        if (apuesta.getEstado() != EstadoApuesta.ABIERTA) {
            throw new ApiException(
                    ErrorCode.BET_ALREADY_PLACED,
                    "La apuesta no está abierta",
                    Map.of("id", idApuesta));
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

    @Override
    public List<ApuestaDTO> findByUsuario(Long idUsuario) {
        log.info("Búsqueda de apuestas por usuario id={}", idUsuario);
        return apuestaRepository.findByUsuario(idUsuario).stream()
                .map(apuestaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApuestaDTO> findTop(int cantidad) {
        log.info("Búsqueda de las {} apuestas con mas total apostado", cantidad);
        return apuestaRepository.findAllOrderByTotalApostado().stream()
                .map(apuestaMapper::toDTO)
                .limit(cantidad)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApuestaDTO> findLast(int cantidad) {
        log.info("Búsqueda de las {} apuestas con mas total apostado", cantidad);
        return apuestaRepository.findAllOrderByFecha().stream()
                .map(apuestaMapper::toDTO)
                .limit(cantidad)
                .collect(Collectors.toList());
    }

    @Override
    public ApuestaDTO eliminarApuesta(Long id) {
        log.info("Eliminando apuesta con id={}", id);
        Apuesta apuesta = apuestaRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BET_NOT_FOUND,
                        "Apuesta no encontrada",
                        Map.of("id", id)));

        switch (apuesta.getEstado()) {
            case ABIERTA:
                apuesta.setEstado(EstadoApuesta.ELIMINADA);
                apuesta.setDescripcion(apuesta.getDescripcion() + " | Apuesta eliminada por el administrador");
                apuesta.setFechaCierre(java.time.LocalDateTime.now());
                ticketService.devolverTickets(id);
                break;
            case CERRADA:
                apuesta.setEstado(EstadoApuesta.ELIMINADA);
                apuesta.setDescripcion(apuesta.getDescripcion() + " | Apuesta eliminada por el administrador");
                ticketService.devolverTickets(id);

                break;
            case CANCELADA:
                apuesta.setEstado(EstadoApuesta.ELIMINADA);
                apuesta.setDescripcion(apuesta.getDescripcion() + " | Apuesta eliminada por el administrador");
                apuesta.setFechaCierre(java.time.LocalDateTime.now());
                break;
            case RESUELTA:
                apuesta.setEstado(EstadoApuesta.ELIMINADA);
                apuesta.setDescripcion(apuesta.getDescripcion() + " | Apuesta eliminada por el administrador");
            default:
                break;
        }
        apuesta = apuestaRepository.save(apuesta);
        return apuestaMapper.toDTO(apuesta);
    }

    @Override
    public List<ApuestaDTO> findAllAdmin(Long idUsuario) {
        log.info("Búsqueda de todas las apuestas para el administrador con id={}", idUsuario);
        usuarioRepository.isAdmin(idUsuario)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado o no es administrador",
                        Map.of("id", idUsuario)));
        log.info("Usuario con id={} es administrador, obteniendo todas las apuestas.");
        List<Apuesta> apuestas = apuestaRepository.findAll();
        log.info("Se encontraron {} apuestas para el administrador con id={}", apuestas.size(), idUsuario);
        List<ApuestaDTO> apuestasDTO = apuestas.stream()
                .map(apuestaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Se retornan {} apuestas para el administrador con id={}", apuestasDTO.size(), idUsuario);
        return apuestasDTO;
    }
}