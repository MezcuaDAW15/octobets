package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;

import com.pfc.octobets.model.dto.UsuarioDTO;
import com.pfc.octobets.repository.entity.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioDTO toDTO(Usuario entity);

    Usuario toEntity(UsuarioDTO dto);
}