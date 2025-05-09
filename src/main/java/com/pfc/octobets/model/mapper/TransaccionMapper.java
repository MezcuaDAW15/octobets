package com.pfc.octobets.model.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pfc.octobets.model.dto.TransaccionDTO;
import com.pfc.octobets.repository.entity.Transaccion;

@Mapper(componentModel = "spring", uses = { CarteraMapper.class })
public interface TransaccionMapper {
    @Mapping(source = "cartera", target = "carteraDTO")
    TransaccionDTO toDTO(Transaccion entity);

    @InheritInverseConfiguration
    Transaccion toEntity(TransaccionDTO dto);
}