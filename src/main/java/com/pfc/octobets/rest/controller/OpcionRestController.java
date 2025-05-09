package com.pfc.octobets.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.OpcionDTO;
import com.pfc.octobets.service.OpcionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ws/apuestas/{idApuesta}/opciones")
@RequiredArgsConstructor
@Slf4j
public class OpcionRestController {

    @Autowired
    private OpcionService opcionService;

    @GetMapping
    public ResponseEntity<List<OpcionDTO>> getAllByApuesta(@PathVariable Long idApuesta) {
        log.info("Petición recibida: obtener todas las opciones de la apuesta.");
        List<OpcionDTO> opciones = opcionService.findAllByApuestaId(idApuesta);
        log.info("Se devuelven {} opciones.", opciones.size());
        return ResponseEntity.ok(opciones);
    }

    @GetMapping("/{idOpcion}")
    public ResponseEntity<OpcionDTO> getOpcionById(@PathVariable Long idApuesta, @PathVariable Long idOpcion) {
        log.info("Petición recibida: obtener opción con id={}", idOpcion);
        OpcionDTO opcion = opcionService.findById(idApuesta, idOpcion);
        log.info("Opción encontrada: {}", opcion);
        return ResponseEntity.ok(opcion);
    }
    

}
