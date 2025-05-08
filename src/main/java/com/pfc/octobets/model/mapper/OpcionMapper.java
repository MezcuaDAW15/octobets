package com.pfc.octobets.model.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pfc.octobets.model.dto.OpcionDTO;
import com.pfc.octobets.repository.entity.Opcion;

@Mapper(componentModel = "spring", uses = { ApuestaMapper.class })
public interface OpcionMapper {
    @Mapping(source = "apuesta", target = "apuestaDTO")
    OpcionDTO toDTO(Opcion opcion);

    @InheritInverseConfiguration
    Opcion toEntity(OpcionDTO dto);
}