package com.pfc.octobets.model.mapper;

import org.mapstruct.Mapper;

import com.pfc.octobets.model.dto.TicketDTO;
import com.pfc.octobets.repository.entity.Ticket;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class, OpcionMapper.class })
public interface TicketMapper {
    TicketDTO toDTO(Ticket entity);

    Ticket toEntity(TicketDTO dto);
}