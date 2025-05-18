package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pfc.octobets.model.dto.OpcionDTO;
import com.pfc.octobets.repository.entity.Opcion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OpcionMapper {
    OpcionDTO toDTO(Opcion opcion);

    Opcion toEntity(OpcionDTO dto);
}