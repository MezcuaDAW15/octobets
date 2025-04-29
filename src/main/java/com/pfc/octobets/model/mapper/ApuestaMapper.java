package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.repository.entity.Apuesta;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class })
public interface ApuestaMapper {
    ApuestaDTO toDTO(Apuesta entity);

    Apuesta toEntity(ApuestaDTO dto);
}