package com.pfc.octobets.service;

import com.pfc.octobets.model.dto.UsuarioDTO;

public interface UsuarioService {

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO, String password);

}