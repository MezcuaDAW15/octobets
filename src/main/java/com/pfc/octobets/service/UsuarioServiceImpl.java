package com.pfc.octobets.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pfc.octobets.common.ApiException;
import com.pfc.octobets.common.ErrorCode;
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
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()) != null) {
            throw new ApiException(
                    ErrorCode.USER_ALREADY_EXISTS,
                    "El email ya está registrado",
                    Map.of("email", usuarioDTO.getEmail()));
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

    @Override
    public UsuarioDTO updateProfile(Long idUsuario, UsuarioDTO dto) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado",
                        Map.of("id", idUsuario)));

        // copia sólo campos editables
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
        usuario.setUsername(dto.getUsername());
        usuario.setAvatar(dto.getAvatar());

        usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public void changePassword(Long idUsuario, String oldPass, String newPass) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado",
                        Map.of("id", idUsuario)));

        if (!passwordEncoder.matches(oldPass, usuario.getPasswordHash())) {
            throw new ApiException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "La contraseña actual no coincide",
                    Map.of("userId", idUsuario));
        }

        usuario.setPasswordHash(passwordEncoder.encode(newPass));
        usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioDTO findById(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toDTO)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado",
                        Map.of("id", id)));
    }

}