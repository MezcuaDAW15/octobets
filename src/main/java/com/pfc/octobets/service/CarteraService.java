package com.pfc.octobets.service;

public interface CarteraService {

    Double getSaldo(Long idUsuario);

    void cobrar(Long id, Double cantidadApostada);

}