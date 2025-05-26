package com.pfc.octobets.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.pfc.octobets.common.ApiException;
import com.pfc.octobets.common.ErrorCode;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.model.mapper.CarteraMapper;
import com.pfc.octobets.repository.dao.CarteraRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CarteraServiceImpl implements CarteraService {

    @Autowired
    private CarteraRepository carteraRepository;

    @Autowired
    private CarteraMapper carteraMapper;

    @Override
    public Double getSaldo(Long idUsuario) {
        log.info("Búsqueda del saldo de la cartera del usuario con id={}", idUsuario);
        return carteraRepository.findById(idUsuario)
                .map(cartera -> cartera.getSaldoFichas())
                .orElseThrow(() -> new ApiException(
                    ErrorCode.USER_NOT_FOUND,
                    "Cartera no encontrada para el usuario",
                    Map.of("id", idUsuario)
                ));
    }

    public CarteraDTO findByUsuario(Long idUsuario) {
        log.info("Búsqueda de la cartera del usuario con id={}", idUsuario);
        return carteraRepository.findById(idUsuario)
                .map(carteraMapper::toDTO)
                .orElseThrow(() -> new ApiException(
                    ErrorCode.USER_NOT_FOUND,
                    "Cartera no encontrada para el usuario",
                    Map.of("id", idUsuario)
                ));
    }

    @Override
    public void cobrar(Long id, Double cantidadApostada) {
        log.info("Cobro de {} a la cartera con id={}", cantidadApostada, id);
        carteraRepository.findById(id)
                .ifPresentOrElse(cartera -> {
                    double nuevoSaldo = cartera.getSaldoFichas() - cantidadApostada;
                    cartera.setSaldoFichas(nuevoSaldo);
                    carteraRepository.save(cartera);
                    log.info("Nuevo saldo de la cartera con id={} es {}", id, nuevoSaldo);
                }, () -> {
                    throw new ApiException(
                        ErrorCode.USER_NOT_FOUND,
                        "Cartera no encontrada para el usuario",
                        Map.of("id", id)
                    );
                });
    }

}