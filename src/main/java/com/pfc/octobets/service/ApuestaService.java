package com.pfc.octobets.service;

import java.util.List;

import com.pfc.octobets.model.dto.ApuestaDTO;

public interface ApuestaService {
    List<ApuestaDTO> findAll();
}