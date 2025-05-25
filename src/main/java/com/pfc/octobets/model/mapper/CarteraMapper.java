package com.pfc.octobets.model.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.repository.entity.Cartera;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarteraMapper {
    @Mapping(source = "idUsuario", target = "id")
    CarteraDTO toDTO(Cartera entity);

    @Mapping(target = "usuario", ignore = true)
    @InheritInverseConfiguration
    Cartera toEntity(CarteraDTO dto);
}