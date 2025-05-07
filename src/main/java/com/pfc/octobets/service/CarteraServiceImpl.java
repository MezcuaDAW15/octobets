package com.pfc.octobets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.repository.dao.CarteraRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CarteraServiceImpl implements CarteraService {

    @Autowired
    private CarteraRepository carteraRepository;

    @Override
    public Double getSaldo(Long idUsuario) {
        log.info("Búsqueda del saldo de la cartera del usuario con id={}", idUsuario);
        return carteraRepository.findById(idUsuario)
                .map(cartera -> cartera.getSaldoFichas())
                .orElseThrow(() -> {
                    String msg = "Cartera no encontrada para el usuario con id=" + idUsuario;
                    log.warn(msg);
                    return new ResourceNotFoundException(msg);
                });
    }

}