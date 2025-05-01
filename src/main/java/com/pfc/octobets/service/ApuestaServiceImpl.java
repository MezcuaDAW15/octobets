package com.pfc.octobets.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.model.mapper.ApuestaMapper;
import com.pfc.octobets.repository.dao.ApuestaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApuestaServiceImpl implements ApuestaService {

    @Autowired
    private ApuestaRepository apuestaRepository;
    @Autowired
    private ApuestaMapper apuestaMapper;

    @Override
    public List<ApuestaDTO> findAll() {
        log.info("Búsqueda de todas las apuestas en el repositorio.");
        return apuestaRepository.findAll().stream()
                .map(apuestaMapper::toDTO)
                .collect(Collectors.toList());
    }
}