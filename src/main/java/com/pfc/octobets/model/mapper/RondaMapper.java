package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;

import com.pfc.octobets.model.dto.RondaDTO;
import com.pfc.octobets.repository.entity.Ronda;

@Mapper(componentModel = "spring", uses = { JuegoMapper.class, UsuarioMapper.class })
public interface RondaMapper {
    RondaDTO toDTO(Ronda entity);

    Ronda toEntity(RondaDTO dto);
}