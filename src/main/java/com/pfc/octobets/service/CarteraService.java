package com.pfc.octobets.service;

import com.pfc.octobets.model.dto.CarteraDTO;

public interface CarteraService {

    Double getSaldo(Long idUsuario);

    void cobrar(Long id, Double cantidadApostada);

    CarteraDTO findByUsuario(Long idUsuario);

}