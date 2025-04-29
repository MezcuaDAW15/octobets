package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;

import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.repository.entity.Cartera;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class })
public interface CarteraMapper {
    CarteraDTO toDTO(Cartera entity);

    Cartera toEntity(CarteraDTO dto);
}