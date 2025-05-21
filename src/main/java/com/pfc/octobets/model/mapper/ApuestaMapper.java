package com.pfc.octobets.model.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.repository.entity.Apuesta;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class, OpcionMapper.class })
public interface ApuestaMapper {
    ApuestaDTO toDTO(Apuesta entity);

    Apuesta toEntity(ApuestaDTO dto);

    @AfterMapping
    default void linkOpciones(@MappingTarget Apuesta apuesta) {
        if (apuesta.getOpciones() != null) {
            apuesta.getOpciones().forEach(o -> o.setApuesta(apuesta));
        }
    }
}