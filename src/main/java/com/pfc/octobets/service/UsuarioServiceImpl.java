package com.pfc.octobets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.UsuarioDTO;
import com.pfc.octobets.model.mapper.UsuarioMapper;
import com.pfc.octobets.repository.dao.CarteraRepository;
import com.pfc.octobets.repository.dao.UsuarioRepository;
import com.pfc.octobets.repository.entity.Cartera;
import com.pfc.octobets.repository.entity.Usuario;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private CarteraRepository carteraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO, String password) {
        log.info("Creando usuario: {}", usuarioDTO);
        if (usuarioRepository.getByEmail(usuarioDTO.getEmail()) != null) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        Usuario usuarioNuevo = usuarioMapper.toEntity(usuarioDTO);
        usuarioNuevo.setPasswordHash(passwordEncoder.encode(password));
        usuarioNuevo = usuarioRepository.save(usuarioNuevo);
        log.info("Usuario creado: {}", usuarioNuevo);

        Cartera cartera = new Cartera(usuarioNuevo.getId(), usuarioNuevo, 0.0);
        cartera = carteraRepository.save(cartera);
        log.info("Cartera creada: {}", cartera);
        return usuarioMapper.toDTO(usuarioNuevo);

    }

}