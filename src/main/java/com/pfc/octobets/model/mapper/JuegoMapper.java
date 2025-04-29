package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;

import com.pfc.octobets.model.dto.JuegoDTO;
import com.pfc.octobets.repository.entity.Juego;

@Mapper(componentModel = "spring")
public interface JuegoMapper {
    JuegoDTO toDTO(Juego entity);

    Juego toEntity(JuegoDTO dto);
}