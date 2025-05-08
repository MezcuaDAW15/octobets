package com.pfc.octobets.model.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.repository.entity.Cartera;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class })
public interface CarteraMapper {
    @Mapping(source = "idUsuario", target = "id")
    CarteraDTO toDto(Cartera cartera);

    @InheritInverseConfiguration
    Cartera toEntity(CarteraDTO dto);
}