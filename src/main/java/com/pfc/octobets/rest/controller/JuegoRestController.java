package com.pfc.octobets.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.JuegoRequestDTO;
import com.pfc.octobets.model.dto.JuegoResponseDTO;
import com.pfc.octobets.service.CarteraService;
import com.pfc.octobets.service.JuegoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ws/apuestas")
@RequiredArgsConstructor
@Slf4j
public class JuegoRestController {

    @Autowired
    private JuegoService juegoService;
    @Autowired
    private CarteraService carteraService;

    @PostMapping("/{tipoJuego}/jugar")
    public ResponseEntity<JuegoResponseDTO> jugar(@PathVariable String tipoJuego,
            @RequestBody JuegoRequestDTO request) {

        log.info("Solicitud recibida para jugar. Tipo de juego: {}, Usuario ID: {}, Cantidad apostada: {}",
                tipoJuego, request.getUsuarioDTO().getId(), request.getCantidadApostada());

        // Validar saldo de usuario
        double saldoUsuario = carteraService.getSaldo(request.getUsuarioDTO().getId());
        if (saldoUsuario < request.getCantidadApostada()) {
            log.error("Saldo insuficiente para el usuario ID: {}. Saldo actual: {}, Cantidad apostada: {}",
                    request.getUsuarioDTO().getId(), saldoUsuario, request.getCantidadApostada());
            return ResponseEntity.badRequest().build(); // Saldo insuficiente
        }

        // Validar tipo de juego
        JuegoResponseDTO resultado;
        if ("ruleta".equalsIgnoreCase(tipoJuego)) {
            log.info("Iniciando lógica de juego para el tipo: {}", tipoJuego);
            resultado = juegoService.jugarRuleta(request);
        } else {
            log.error("Tipo de juego no soportado: {}", tipoJuego);
            return ResponseEntity.badRequest().build(); // Tipo de juego no soportado
        }

        log.info("Juego completado. Resultado: {}, Fichas ganadas: {}",
                resultado.isResultado() ? "Ganador" : "Perdedor", resultado.getFichasGanadas());

        return ResponseEntity.ok(resultado);
    }
}
