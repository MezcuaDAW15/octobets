package com.pfc.octobets.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.service.ApuestaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
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
}
