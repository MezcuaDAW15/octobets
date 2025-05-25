package com.pfc.octobets.service;

import com.pfc.octobets.model.dto.AuthRequestDTO;
import com.pfc.octobets.model.dto.AuthResponseDTO;
import com.pfc.octobets.model.dto.RegisterRequestDTO;
import com.pfc.octobets.model.dto.UsuarioDTO;

public interface AuthService {

    AuthResponseDTO login(AuthRequestDTO req);

    AuthResponseDTO register(RegisterRequestDTO req);

    UsuarioDTO currentUser(String bearerToken);
}
