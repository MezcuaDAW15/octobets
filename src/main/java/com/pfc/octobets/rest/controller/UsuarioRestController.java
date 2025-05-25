package com.pfc.octobets.rest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.UsuarioDTO;
import com.pfc.octobets.security.UsuarioDetails;
import com.pfc.octobets.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ws/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
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

    @GetMapping("/mi-perfil")
    public ResponseEntity<UsuarioDTO> me(@AuthenticationPrincipal UsuarioDetails ud) {
        return ResponseEntity.ok(usuarioService.findById(ud.getId()));
    }

    @PutMapping("/mi-perfil")
    public ResponseEntity<UsuarioDTO> update(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody UsuarioDTO dto) {

        UsuarioDTO updated = usuarioService.updateProfile(ud.getId(), dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/mi-perfil/password")
    public ResponseEntity<Void> password(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody Map<String, String> body) {

        usuarioService.changePassword(
                ud.getId(),
                body.getOrDefault("oldPassword", ""),
                body.getOrDefault("newPassword", ""));
        return ResponseEntity.noContent().build();
    }

}
