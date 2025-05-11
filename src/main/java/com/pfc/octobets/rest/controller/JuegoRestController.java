package com.pfc.octobets.rest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.JuegoRequestDTO;
import com.pfc.octobets.model.dto.JuegoResponseDTO;
import com.pfc.octobets.model.enums.Juego;
import com.pfc.octobets.service.CarteraService;
import com.pfc.octobets.service.JuegoService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador unificado de partidas de juego.
 * <p>
 * Gestiona todas las solicitudes de juego (ruleta, tragaperras, blackjack,
 * etc.)
 * a través de un único endpoint parametrizado por tipo de juego.
 * </p>
 */
@RestController
@RequestMapping("/ws/juegos")
@Slf4j
public class JuegoRestController {

    @Autowired
    private JuegoService juegoService;

    @Autowired
    private CarteraService carteraService;

    /**
     * Inicia o avanza la partida de un juego determinado.
     *
     * @param tipoJuego identificador del juego ("ruleta", "tragaperras",
     *                  "blackjack", ...)
     * @param request   datos de la apuesta y estado de la partida en curso
     * @return DTO con el estado actualizado de la partida y, si corresponde, la
     *         ganancia
     */
    @PostMapping("/{tipoJuego}/jugar")
    public ResponseEntity<JuegoResponseDTO> jugar(
            @PathVariable String tipoJuego,
            @RequestBody JuegoRequestDTO request) {

        long idUsuario = request.getUsuarioDTO().getId();
        double apuesta = request.getCantidadApostada();
        log.info("→ [{}] Inicio de partida: idUsuario={}, apuesta={}",
                tipoJuego, idUsuario, apuesta);

        // Validar saldo suficiente
        double saldo = carteraService.getSaldo(idUsuario);
        if (saldo < apuesta) {
            log.warn("Saldo insuficiente para idUsuario={}: saldo={} < apuesta={}",
                    idUsuario, saldo, apuesta);
            return ResponseEntity.badRequest().body(
                    new JuegoResponseDTO(false, 0, Map.of("error", "Saldo insuficiente")));
        }

        // Convertir tipoJuego a enum para evitar strings arbitrarios
        Juego juegoEnum;
        try {
            juegoEnum = Juego.valueOf(tipoJuego.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.error("Tipo de juego no soportado: '{}'", tipoJuego);
            return ResponseEntity.badRequest().body(
                    new JuegoResponseDTO(false, 0, Map.of("error", "Juego no válido")));
        }

        // Despacho a la lógica específica
        JuegoResponseDTO resultado;
        switch (juegoEnum) {
            case RULETA:
                log.debug("Despachando a jugarRuleta()");
                resultado = juegoService.jugarRuleta(request);
                break;
            case TRAGAPERRAS:
                log.debug("Despachando a jugarTragaperras3x5()");
                resultado = juegoService.jugarTragaperras3x5(request);
                break;
            case BLACKJACK:
                log.debug("Despachando a jugarBlackjack()");
                resultado = juegoService.jugarBlackjack(request);
                break;
            case PLINKO:
                log.debug("Despachando a jugarPlinko()");
                resultado = juegoService.jugarPlinko(request);
                break;
            default:
                log.error("Juego implementado no encontrado en switch: {}", juegoEnum);
                return ResponseEntity.badRequest().build();
        }

        log.info("[{}] Finalizada: resultado={}, ganancia={}",
                tipoJuego, resultado.isResultado(), resultado.getFichasGanadas());

        return ResponseEntity.ok(resultado);
    }
}