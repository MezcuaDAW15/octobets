package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;

import com.pfc.octobets.model.dto.TransaccionDTO;
import com.pfc.octobets.repository.entity.Transaccion;

@Mapper(componentModel = "spring", uses = { CarteraMapper.class })
public interface TransaccionMapper {
    TransaccionDTO toDTO(Transaccion entity);

    Transaccion toEntity(TransaccionDTO dto);
}