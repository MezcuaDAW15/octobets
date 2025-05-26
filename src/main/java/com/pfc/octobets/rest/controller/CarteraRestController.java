package com.pfc.octobets.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.service.CarteraService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ws/carteras")
@RequiredArgsConstructor
@Slf4j
public class CarteraRestController {

    @Autowired
    private CarteraService carteraService;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<CarteraDTO> getCarteraByUsuario(@PathVariable Long idUsuario) {
        log.info("Petición recibida: obtener cartera por usuario id={}", idUsuario);
        CarteraDTO cartera = carteraService.findByUsuario(idUsuario);
        if (cartera != null) {
            log.info("Cartera encontrada: {}", cartera);
            return ResponseEntity.ok(cartera);
        } else {
            log.warn("No se encontró la cartera para el usuario id={}", idUsuario);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
