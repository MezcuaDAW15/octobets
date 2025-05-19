package com.pfc.octobets.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t FROM Ticket t WHERE t.opcion.id = :idOpcion")
    List<Ticket> findByOpcionId(Long idOpcion);

    @Query("""
               SELECT DISTINCT t
               FROM   Ticket  t
                 JOIN FETCH t.opcion o
                 JOIN FETCH o.apuesta a
                 LEFT JOIN FETCH a.opciones
               WHERE  t.usuario.id = :idUsuario
            """)
    List<Ticket> findByUsuarioId(Long idUsuario);

}
