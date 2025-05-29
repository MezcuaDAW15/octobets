package com.pfc.octobets.repository.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Apuesta;

@Repository
public interface ApuestaRepository extends JpaRepository<Apuesta, Long> {
    @EntityGraph(attributePaths = "opciones")
    @Query("SELECT a FROM Apuesta a ")
    List<Apuesta> findAll();

    @EntityGraph(attributePaths = "opciones")
    @Query("SELECT a FROM Apuesta a WHERE a.estado = 'ABIERTA'")
    List<Apuesta> findAllAbiertas();

    @EntityGraph(attributePaths = "opciones")
    Optional<Apuesta> findById(Long id);

    @EntityGraph(attributePaths = "opciones")
    @Query("SELECT a FROM Apuesta a WHERE a.creador.id = ?1")
    List<Apuesta> findByUsuario(Long idUsuario);

    @Query("""
              SELECT a
                FROM Apuesta a
               WHERE a.estado = 'ABIERTA'
            ORDER BY (
                  SELECT COALESCE(SUM(o.totalApostado), 0)
                    FROM Opcion o
                   WHERE o.apuesta = a
              ) DESC
              """)
    @EntityGraph(attributePaths = "opciones")
    List<Apuesta> findAllOrderByTotalApostado();

    /**
     * Trae todas las apuestas ABIERTAS ordenadas
     * por fechaCreacion (de más reciente a más antigua).
     * Las opciones se fetch-ean gracias al EntityGraph.
     */
    @Query("""
              SELECT a
                FROM Apuesta a
               WHERE a.estado = 'ABIERTA'
            ORDER BY a.fechaCreacion DESC
              """)
    @EntityGraph(attributePaths = "opciones")
    List<Apuesta> findAllOrderByFecha();

}
