package com.pfc.octobets.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.model.dto.TicketDTO;
import com.pfc.octobets.service.ApuestaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/ws/apuestas")
@RequiredArgsConstructor
@Slf4j
public class ApuestaRestController {

    @Autowired
    private ApuestaService apuestaService;

    @GetMapping
    public ResponseEntity<List<ApuestaDTO>> getAllApuestas() {
        log.info("Petición recibida: obtener todas las apuestas.");
        List<ApuestaDTO> apuestas = apuestaService.findAll();
        log.info("Se retornan {} apuestas.", apuestas.size());
        return ResponseEntity.ok(apuestas);
    }

    @GetMapping("/top")
    public ResponseEntity<List<ApuestaDTO>> getTopApuestas() {
        log.info("Petición recibida: obtener 10 apuestas con mas fichas.");
        List<ApuestaDTO> apuestas = apuestaService.findTop(10);
        log.info("Se retornan {} apuestas.", apuestas.size());
        return ResponseEntity.ok(apuestas);
    }

    @GetMapping("/last")
    public ResponseEntity<List<ApuestaDTO>> getLastApuestas() {
        log.info("Petición recibida: obtener 10 apuestas mas recientes.");
        List<ApuestaDTO> apuestas = apuestaService.findLast(10);
        log.info("Se retornan {} apuestas.", apuestas.size());
        return ResponseEntity.ok(apuestas);
    }

    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<List<ApuestaDTO>> getApuestasByUsuario(@PathVariable Long idUsuario) {
        log.info("Petición recibida: obtener apuestas por usuario id={}", idUsuario);
        List<ApuestaDTO> apuestas = apuestaService.findByUsuario(idUsuario);
        log.info("Se retornan {} apuestas para el usuario id={}", apuestas.size(), idUsuario);
        return ResponseEntity.ok(apuestas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApuestaDTO> getApuestaById(@PathVariable Long id) {
        log.info("Petición recibida: obtener apuesta con id={}", id);
        ApuestaDTO apuesta = apuestaService.findById(id);
        log.info("Apuesta encontrada: {}", apuesta);
        return ResponseEntity.ok(apuesta);
    }

    @PostMapping
    public ResponseEntity<ApuestaDTO> crearApuesta(@RequestBody ApuestaDTO apuestaDTO) {
        log.info("Petición recibida: crear apuesta. + {}", apuestaDTO);
        ApuestaDTO nueva = apuestaService.crearApuesta(apuestaDTO, apuestaDTO.getIdCreador());
        log.info("Apuesta creada: {}", nueva);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApuestaDTO> actualizarApuesta(
            @PathVariable Long id,
            @RequestBody ApuestaDTO apuestaDTO) {
        log.info("Petición recibida: actualizar apuesta id={}", id);
        ApuestaDTO actualizada = apuestaService.actualizarApuesta(id, apuestaDTO);
        log.info("Apuesta actualizada: {}", actualizada);
        return ResponseEntity.ok(actualizada);
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<ApuestaDTO> cerrarApuesta(@PathVariable Long id) {
        log.info("Petición recibida: cerrar apuesta id={}", id);
        ApuestaDTO cerrada = apuestaService.cerrarApuesta(id);
        log.info("Apuesta cerrada: {}", cerrada);
        return ResponseEntity.ok(cerrada);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApuestaDTO> cancelarApuesta(@PathVariable Long id, @RequestBody String motivo) {
        log.info("Petición recibida: cancelar apuesta id={}", id);
        ApuestaDTO cancelada = apuestaService.cancelarApuesta(id, motivo);
        log.info("Apuesta cancelada: {}", cancelada);
        return ResponseEntity.ok(cancelada);
    }

    @PostMapping("/{id}/eliminar")
    public ResponseEntity<ApuestaDTO> eliminarApuesta(@RequestBody String entity) {
        log.info("Petición recibida: eliminar apuesta con id={}", entity);
        Long id = Long.parseLong(entity);
        ApuestaDTO eliminada = apuestaService.eliminarApuesta(id);
        log.info("Apuesta eliminada: {}", eliminada);
        return ResponseEntity.ok(eliminada);
    }

    @PutMapping("/{id}/resolver/{idOpcionGanadora}")
    public ResponseEntity<ApuestaDTO> resolverApuesta(@PathVariable Long id,
            @PathVariable Long idOpcionGanadora) {
        log.info("Petición recibida: resolver apuesta id={} con opción ganadora id={}", id, idOpcionGanadora);

        ApuestaDTO resuelta = apuestaService.resolverApuesta(id, idOpcionGanadora);
        log.info("Apuesta resuelta: {}", resuelta);
        return ResponseEntity.ok(resuelta);
    }

    @PostMapping("/{id}/opciones/{opcionId}")
    public ResponseEntity<String> realizarApuesta(
            @PathVariable Long id,
            @PathVariable Long opcionId,
            @RequestBody TicketDTO ticketDTO) {

        log.info("Petición recibida: realizar apuesta en apuestaId={}, opcionId={}, usuarioId={}, cantidad={}",
                id,
                opcionId,
                ticketDTO.getUsuario().getId(),
                ticketDTO.getCantidadFichas());

        try {
            apuestaService.realizarApuesta(ticketDTO, id, opcionId);
            return ResponseEntity.ok("Apuesta realizada con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error inesperado");
        }
    }

    @GetMapping("/admin/{idUsuario}")
    public ResponseEntity<List<ApuestaDTO>> getAllApuestasAdmin(@PathVariable Long idUsuario) {
        log.info("Petición recibida: obtener todas las apuestas.");
        List<ApuestaDTO> apuestas = apuestaService.findAllAdmin(idUsuario);
        log.info("Se retornan {} apuestas.", apuestas.size());
        return ResponseEntity.ok(apuestas);
    }

    @DeleteMapping("/{idApuesta}/admin/{idUsuario}")
    public ResponseEntity<ApuestaDTO> eliminarApuesta(
            @PathVariable Long idUsuario,
            @PathVariable Long idApuesta) {

        log.info("Petición recibida: eliminar apuesta con id={} por el usuario id={}", idApuesta, idUsuario);
        ApuestaDTO eliminada = apuestaService.eliminarApuesta(idApuesta);
        log.info("Apuesta eliminada: {}", eliminada);
        return ResponseEntity.ok(eliminada);
    }

}
