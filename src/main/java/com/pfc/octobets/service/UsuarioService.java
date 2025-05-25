package com.pfc.octobets.service;

import com.pfc.octobets.model.dto.UsuarioDTO;

public interface UsuarioService {

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO, String password);

    UsuarioDTO updateProfile(Long idUsuario, UsuarioDTO dto);

    void changePassword(Long idUsuario, String oldPass, String newPass);

    UsuarioDTO findById(Long id);

}