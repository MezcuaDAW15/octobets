package com.pfc.octobets.service;

import com.pfc.octobets.model.dto.JuegoRequestDTO;
import com.pfc.octobets.model.dto.JuegoResponseDTO;

public interface JuegoService {
    JuegoResponseDTO jugarRuleta(JuegoRequestDTO dto);

}
