package com.pfc.octobets.model.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.pfc.octobets.model.dto.ApuestaDTO;
import com.pfc.octobets.repository.entity.Apuesta;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class, OpcionMapper.class })
public interface ApuestaMapper {
    @Mapping(source = "creador.id", target = "idCreador")
    public abstract ApuestaDTO toDTO(Apuesta entity);

    @Mapping(target = "creador", ignore = true)
    public abstract Apuesta toEntity(ApuestaDTO dto);

    @AfterMapping
    default void linkOpciones(@MappingTarget Apuesta apuesta) {
        if (apuesta.getOpciones() != null) {
            apuesta.getOpciones().forEach(o -> o.setApuesta(apuesta));
        }
    }
}