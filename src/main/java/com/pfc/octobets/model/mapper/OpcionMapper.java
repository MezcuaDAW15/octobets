package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;

import com.pfc.octobets.model.dto.OpcionDTO;
import com.pfc.octobets.repository.entity.Opcion;

@Mapper(componentModel = "spring", uses = { ApuestaMapper.class })
public interface OpcionMapper {
    OpcionDTO toDTO(Opcion entity);

    Opcion toEntity(OpcionDTO dto);
}