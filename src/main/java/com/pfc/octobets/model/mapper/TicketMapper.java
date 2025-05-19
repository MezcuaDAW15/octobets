package com.pfc.octobets.model.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pfc.octobets.model.dto.TicketDTO;
import com.pfc.octobets.repository.entity.Ticket;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class, OpcionMapper.class })
public interface TicketMapper {
    @Mapping(source = "opcion.apuesta.titulo", target = "apuestaTitulo")
    @Mapping(source = "opcion.apuesta.descripcion", target = "apuestaDescripcion")
    @Mapping(source = "opcion.apuesta.tipo", target = "apuestaTipo")
    @Mapping(source = "opcion.apuesta.estado", target = "apuestaEstado")
    TicketDTO toDTO(Ticket entity);

    @InheritInverseConfiguration
    @Mapping(target = "opcion.apuesta", ignore = true)
    Ticket toEntity(TicketDTO dto);
}