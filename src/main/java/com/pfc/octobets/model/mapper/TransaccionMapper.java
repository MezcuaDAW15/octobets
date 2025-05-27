package com.pfc.octobets.model.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pfc.octobets.model.dto.TransaccionDTO;
import com.pfc.octobets.repository.entity.Transaccion;

@Mapper(componentModel = "spring", uses = { CarteraMapper.class })
public interface TransaccionMapper {

    TransaccionDTO toDTO(Transaccion entity);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cartera", ignore = true)
    Transaccion toEntity(TransaccionDTO dto);
}