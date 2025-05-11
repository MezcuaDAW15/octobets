package com.pfc.octobets.service;

public interface CarteraService {

    Double getSaldo(Long idUsuario);

    void pagar(Long id, double ganancia);

    void cobrar(Long id, Double cantidadApostada);

}