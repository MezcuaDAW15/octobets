package com.pfc.octobets.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.UsuarioDTO;
import com.pfc.octobets.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ws/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping()
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioDTO usuarioDTO, @RequestBody String password) {
        log.info("Petición recibida: crear usuario.");
        UsuarioDTO nuevo = usuarioService.crearUsuario(usuarioDTO, password);
        log.info("Usuario creado: {}", nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

}
